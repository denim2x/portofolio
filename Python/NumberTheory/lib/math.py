from libnum import invmod as _invmod
from gmpy_cffi import mpz
from random import randint, random, choice

def isscalar(n):
	return not hasattr(n, '__iter__')

class Infinity:
	def __rmod__(self, d):
		return d

	def __lt__(self, x):
		return False

infinity = Infinity()

def modulus(m):
	if m < 2:
		raise ArithmeticError('Modulus cannot be less than 2.')
	return m

def sum(seq, base=infinity):
	base = modulus(base)
	res = 0
	for e in seq:
		res = (res + e) % base
	return res

def prod(seq, base=infinity):
	base = modulus(base)
	res = 1
	for e in seq:
		res = (res * e) % base
	return res

def to_base(n, base):
	a = []
	base = modulus(base)
	while n > 0:
		a.append(n % base)
		n = n / base
	a = [0] + a
	return Polynomial(a)

def invmod(n, m):
	if m is infinity:
		return 1 / n
	return _invmod(n, m)

def expmod(b, e, m):
	if e < 0:
		b = invmod(b, m)
		return expmod(b, -e, m)
	c = 1
	while e > 0:
		if e & 1 == 1:
			c = (c * b) % m
		b = (b * b) % m
		e >>= 1
	return c % m

def lsb(n, m=1):
	return n % (1 << m)

def randbool(p):
	return random() <= p

class Vector(list):
	def __init__(self, seq, str_func=str):
		list.__init__(self, seq)
		self.str_func = str_func

	def __repr__(self):
		str_func = self._str_func
		return '(%s)' % ', '.join(str_func(e) for e in self)

	def _set_str_func(self, str_func):
		self._str_func = str_func if hasattr(str_func, '__call__') else str

	str_func = property(lambda o: o._str_func, _set_str_func)

class Polynomial(Vector):
	def __init__(self, seq, str_func=str):
		Vector.__init__(self, seq, str_func)
		self.degree = len(self) - 1

		def ex(self, *args):
			raise SyntaxError('Polynomial objects are immutable.')

		for e in 'setitem setslice delitem delslice'.split(' '):
			setattr(self, '__%s__' % e, ex)

	def __add__(a, b):
		b, base = infinity[b]
		c = 'str_func'
		str_func = getattr(b, c) if hasattr(b, c) else getattr(a, c)
		if len(a) > len(b):
			a, b = b, a
		# Now len(a) <= len(b) for sure
		c = list(b)
		for i in range(len(a)):
			c[i] = (a[i] + b[i]) % base
		return Polynomial(c, str_func)

	def __mul__(a, b):
		b, base = infinity[b]
		c = 'str_func'
		str_func = getattr(b, c) if hasattr(b, c) else getattr(a, c)
		n = len(a) + len(b)
		c = [0] * (n - 1)
		for i in range(0, len(a)):
			for j in range(0, len(b)):
				c[i + j] = ((a[i] * b[j]) % base + c[i + j]) % base
		return Polynomial(c, str_func)

	def __div__(a, d):
		d, base = infinity[d]
		d = invmod(d, base)
		return Polynomial(((e * d) % base for e in a), a.str_func)

	def __iadd__(a, b):
		return Polynomial.__add__(a, b)

	def __imul__(a, b):
		return Polynomial.__mul__(a, b)

	def __call__(a, x, base=infinity):
		y = a[-1]
		for k in a[-2::-1]:
			y = ((x * y) % base + k) % base
		return y

	def __repr__(self):
		str_func = self._str_func
		return '(%s)' % ', '.join(str_func(e) for e in reversed(self))

	@staticmethod
	def lagrange(x, y, base=infinity):
		s = str
		for e in [y, x]:
			if hasattr(e, 'str_func'):
				s = e.str_func
				break
		poly = lambda *seq: Polynomial(seq, s)
		n = len(x)
		p = poly(0)
		for i in xrange(n):
			pt = poly(y[i])
			for j in xrange(n):
				if j == i:
					continue
				d = (x[i] - x[j]) % base
				pt = pt * (poly(1, -x[j] % base) / (d, base), base)
			p = p + (pt, base)
		return p