#!/usr/bin/python
from collections.abc import Sequence
from random import uniform, random
from math import copysign
import threading, time, inspect

def start_new_thread(func, args=()):
	threading.Thread(target=func, args=args).start()

epsilon = 10 ** -15

def ltrim(text):
	return text.lstrip()

def indentSize(text):
	count = 0
	for c in text:
		if c.isspace():
			count += 1
		else:
			break
	return count

class ArgumentException(Exception):
	def __init__(self, message):
		super().__init__(message)

class List(list):
	pass

def splice(delim, *args):
	return delim.join(args)

def reverse(seq):
	n = len(seq)
	for i in range(n // 2):
		temp = seq[i]
		seq[i] = seq[n - i - 1]
		seq[n - i - 1] = temp
	return seq

def stamp(*args):
	print(*args)
	exit()

def printf(obj, _end=''):
	print(obj, end=_end)

def string(seq):
	return ''.join(seq)

def enum(seq):
	return enumerate(seq)

def _super(self):
	return super(type(self), self)

def _base(self):
	return super(type(self), self).__init__

def _cls(self):
	return self.__class__.__name__

def randSeq(size, lo, up, null, dec=None):
	seq = []
	null /= 100
	for i in range(size):
		val = uniform(lo, up) if i == 0 or random() > null else 0
		if dec is not None:
			val = round(val, dec)
		seq.append(val)
	return seq

def flatten(seq, tuples=False):
	_type = Sequence if tuples else list
	if not isinstance(seq, _type):
		return seq
	flat = List()
	flat.complete = True

	def run(obj, lim):
		if lim < 0:
			flat.complete = False
			return
		if isinstance(obj, _type):
			for e in obj:
				run(e, lim - 1)
		else:
			flat.append(obj)

	run(seq, flatten._limit)
	return flat

flatten._limit = 30

def sign(x):
	return copysign(1, x)

def remember(index):
	def deco(func):
		def _getattr(obj):
			return getattr(obj, '__prev__', (None, None))

		def new(arg, *args):
			key, val = _getattr(func)
			if key != arg:
				val = func(arg, *args)
				func.__prev__ = (arg, val)
			# else: print('@remember: %s' % getattr(func, '__qualname__'))
			return val

		def new2(self, arg, *args):
			key, val = _getattr(self)
			if key != arg:
				val = func(self, arg, *args)
				self.__prev__ = (arg, val)
			# else: print('@remember: %s' % getattr(self, '__qualname__'))
			return val

		return new if index < 1 else new2

	return deco

class Range():
	def __init__(self, start, end):
		self._data = (start, end)

	def __contains__(self, item):
		start, end = self._data
		return start <= item <= end

@remember(0)
def abs(number):
	return builtins.abs(number)

def spawn(secs):
	stack = inspect.stack()
	try:
		src = stack[1]
		mod = src[0]
		env = mod.f_globals
		mod = inspect.getmodule(mod)
		src = src[2]
	finally:
		del stack

	src = inspect.getsourcelines(mod)[0][src:]
	size = indentSize(src[0])
	code = [ltrim(src[0])]
	for e in src[1:]:
		if indentSize(e) >= size:
			code.append(ltrim(e))
		else:
			break

	code = string(code)
	def run():
		time.sleep(secs)
		exec(code, env)

	start_new_thread(run)
	return False
