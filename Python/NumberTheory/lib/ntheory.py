from gmpy_cffi.ntheory import *
from random import randint

class AdditionChain(list):
	def __init__(self, n):
		c = []
		while n >= 1:
			c.append(n)
			if n % 2 == 0:
				n >>= 1
			else:
				n -= 1
		list.__init__(self, reversed(c))

def rsm(a, k, n):
	assert a % n == a
	assert 0 <= k < n
	b = 1
	if k == 0:
		return b
	t = k.bit_length()
	if k[0] == 1:
		b = a
	a = mpz(a)
	for i in range(1, t):
		a = a ** (2, n)
		if k[i] == 1:
			b = (a * b) % n
	return b

def Jacobi(a, b):
	assert a % 2 != 0
	if b in [0, 1]:
		return b
	if b < 0 or b % 2 == 0:
		return 0
	assert 3 <= a < b
	i = 1
	if a < 0:
		a = -a
		if b % 4 == 3:
			i = -i
	while a != 0:
		while a % 2 == 0:
			a /= 2
			if b % 8 in [3, 5]:
				i = -i
		a, b = b, a
		if a % 4 == 3 and b % 4 == 3:
			i = -i
		a %= b
	if b == 1:
		return i
	return 0
			
class SolovayStrassen:
	@staticmethod
	def test(n, t=100):
		if n < 2:
			return False
		if n == 2:
			return True
		if n % 2 == 0:
			return False
		for i in range(t):
			a = randint(2, n - 2)
			r = rsm(a, (n-1) / 2, n)
			if r != 1 and r != n - 1:
				return False
			s = Jacobi(a, n)
			if r % n != s % n:
				return False
		return True



