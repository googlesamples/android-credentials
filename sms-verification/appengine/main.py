"""`main` is the top level module for your Flask application."""

# Import the Flask Framework
import logging
from google.appengine.api import memcache
from flask import Flask, request
import json
import random
import re

from twilio import twiml
from twilio.rest import TwilioRestClient
import credentials

app = Flask(__name__)
twilioCreds = credentials.CONFIG['twilio']
clients = credentials.CONFIG['clients']

twilioClient = TwilioRestClient(twilioCreds['sid'], twilioCreds['auth_token'])

@app.route('/')
def hello():
  """Return a friendly HTTP greeting."""
  return 'Hello World!'

@app.route('/api/request', methods=['POST'])
def request_verify():
  """Request an OTP to be sent to a device"""
  try:
    params = get_json(['client_secret', 'phone'])
    verifier = SmsVerifier(params['client_secret'], params['phone'])
  except ValueError as err:
    return json.dumps({'success': False, 'msg': str(err)}), 404
  except AssertionError as err:
    return json.dumps({'success': False, 'msg': str(err)}), 401

  rv = verifier.verify()
  return json.dumps({'success': True, 'time': SmsVerifier.EXPIRATION})

@app.route('/api/verify', methods=['POST'])
def do_verify():
  """Verify the OTP is valid for the device"""
  try:
    params = get_json(['client_secret', 'phone', 'sms_message'])
    verifier = SmsVerifier(params['client_secret'], params['phone'])
  except ValueError as err:
    return json.dumps({'success': False, 'msg': str(err)}), 404
  except AssertionError as err:
    return json.dumps({'success': False, 'msg': str(err)}), 401

  rv = verifier.test(params['sms_message'])
  if not rv:
    return json.dumps({'success': False, 'msg': "Unable to validate the OTP"})
  return json.dumps({'success': True, 'phone': params['phone']})

@app.route('/api/reset', methods=['POST'])
def request_reset():
  """Reset the stored OTP for a device"""
  try:
    params = get_json(['client_secret', 'phone'])
    verifier = SmsVerifier(params['client_secret'], params['phone'])
  except ValueError as err:
    return json.dumps({'success': False, 'msg': str(err)}), 404
  except AssertionError as err:
    return json.dumps({'success': False, 'msg': str(err)}), 401

  rv = verifier.reset()
  return json.dumps({'success': rv, 'phone': params['phone']})

def get_json(keys=[]):
  params = request.get_json()
  logging.info(params)

  if not params:
    raise ValueError("Unable to decode request")

  for key in keys:
    if not key in params:
      raise ValueError("Unable to decode " + key)

  return params

@app.errorhandler(404)
def page_not_found(e):
  """Return a custom 404 error."""
  return create_error('Sorry, Nothing at this URL.', 404)

@app.errorhandler(500)
def application_error(e):
  """Return a custom 500 error."""
  return create_error('Sorry, unexpected error: {}'.format(e), 500)

def create_error(message, code=500):
  return json.dumps({'success': False, 'msg': message}), code

class SmsVerifier:
  RANGE_FROM=0
  RANGE_TO=999999
  EXPIRATION=900 # 15 mins
  PHONE_KEY="%s:phone:%s"

  def __init__(self, client_id, phone, debug=False):
    if not client_id in clients:
      raise AssertionError("Unkown Client")
    config = clients[client_id]
    self.cache_prefix = config['cache_prefix']
    self.sms_template = config['sms_template']
    self.app_hash = config['app_hash']
    self.force_otp = config['force_otp'] if 'force_otp' in config else False
    self.debug = config['debug'] if 'debug' in config else False

    self.phone = SmsVerifier.SANITIZE_PHONE(phone)
    logging.info('Phone number:%s' % (self.phone))

  def verify(self):
    current = self.__get_otp()
    if current:
      logging.info("Cache hit found, not messaging")
      return current

    current = self.__generate_otp()
    if not self.__set_otp(current):
      raise InternalError("Unable to store to memcache")
    self.__send_sms(self.sms_template % ({
      'otp': current, 'app_hash': self.app_hash,}))
    return current

  def test(self, message):
    current = self.__get_otp()
    if not current:
      logging.info("Testing unverified phone number")
      return False

    if not re.search(r"\b%s\b" % current, message, re.I):
      logging.info("Unable to verify presence of OTP")
      return False

    logging.info("Success with message: " + message)
    return True

  def reset(self):
    if self.__get_otp():
      memcache.delete(key=self.__get_phone_key())
      return True
    return False

  def get_phone(self):
    return self.phone

  def __get_otp(self):
    return memcache.get(key=self.__get_phone_key())

  def __set_otp(self, number):
    return memcache.add(key=self.__get_phone_key(), value=number,
      time=SmsVerifier.EXPIRATION)

  def __generate_otp(self):
    if self.force_otp:
      return self.force_otp

    random.seed(str(random.random()) + self.phone)
    current = random.randint(SmsVerifier.RANGE_FROM, SmsVerifier.RANGE_TO)
    return str(current).zfill(6)

  def __get_phone_key(self):
    return SmsVerifier.PHONE_KEY % (self.cache_prefix, self.phone)

  def __send_sms(self, message):
    if self.debug:
      logging.info("[DEBUG] Not sending message: %s" % (message))
      return False
    return twilioClient.messages.create(to=self.phone,
      from_=twilioCreds['sender'], body=message)

  @staticmethod
  def SANITIZE_PHONE(phone):
    phone = re.sub('[^0-9\+]','', phone)
    logging.info(phone)
    if not phone.startswith('+'):
      phone = '+' + phone
    return phone
