#!/usr/bin/python
from helpers import *
from collections.abc import Iterable
from random import uniform
from concurrent.futures import ThreadPoolExecutor

# noinspection PyMissingConstructor
class Polynomial():
	def __init__(self, *args):
		obj = args[0]
		if isinstance(obj, Iterable):
			self._seq = list(obj)

		else:
			self._seq = args
		if abs(self._seq[0]) <= epsilon:
			raise ArgumentException('First coefficient cannot be zero.')
		self.degree = len(self._seq) - 1
		self._data = None
		self._str = None
		self._repr = None

	@remember(1)
	def __call__(self, val):
		A = self._seq
		b = A[0]
		for a in A[1:]:
			b = a + b * val
		return b

	def __lshift__(self, times):  # derivative (when times is 1)
		n = self.degree
		new = []
		for coef, exp in list(self)[:-1]:
			new.append(coef * exp)
		return Polynomial(new)

	def _solve(self, kmax, epsilon, x):
		P, P1, P2, lost = self._data
		for k in range(0, kmax):
			d = 2 * P1(x) ** 2 - P(x) * P2(x)
			if abs(d) < epsilon:
				return None
			dx = 2 * P(x) * P1(x) / d
			x -= dx
			if lost(dx):
				break
		return x if abs(dx) < epsilon else None

	def solve(self, kmax, fact=8):
		if not self._data:
			P = self
			P1 = P << 1
			P2 = P1 << 1
			A = max(abs(e) for e in P[1:])
			a0 = abs(P[0])
			R = (a0 + A) / a0
			span = Range(epsilon, 10 ** 8)
			lost = lambda dx: abs(dx) not in span
			self._data = (P, P1, P2, lost)

		n = self.degree
		temp = []
		with ThreadPoolExecutor(n * fact) as e:
			for i in range(e._max_workers):
				x = uniform(-R, R)
				r = e.submit(self._solve, kmax, epsilon, x)
				temp.append(r)

		sol = []
		for r in temp:
			v = r.result()
			if v is None:
				continue
			for e in sol:
				if abs(e - v) <= epsilon:
					v = None
					break
			if v is not None:
				sol.append(v)

		return sorted(sol)

	def __reversed__(self):
		n = self.degree
		for i, coef in enum(self[::-1]):
			yield coef, n - i

	def __iter__(self):
		n = self.degree
		for i, coef in enum(self[:]):
			yield coef, n - i

	def __getitem__(self, item):
		return self._seq[item]

	def __str__(self):
		if self._str is None:
			n = self.degree
			body = []
			for i, val in enum(self[:]):
				if val == 0: continue
				if i > 0 and val > 0 and len(body) > 0:
					body.append(' + ')
				elif val < 0:
					body.append(' - ' if len(body) > 0 else '-')

				val = abs(val)
				if i < n:
					if val > 1:
						body.append(str(val))

					body.append('x^%d' % (n - i) if i < n - 1 else 'x')
				else:
					body.append(str(val))

			body = string(body) if len(body) > 0 else str(0)
			self._str = '%s(%s): %s' % (_cls(self), n, body)
		return self._str

	def __repr__(self):
		if self._repr is None:
			self._repr = str(self._seq)
		return self._repr

	class Lagrange:
		def __init__0(self, _map):
			n = len(_map)
			x = list(_map.keys())
			y = list(_map.values())

		def __init__(self, x, y):
			n = len(x)
			for j in range(1, n):
				for i in range(n - 1, j - 1, -1):
					y[i] = (y[i] - y[i - 1]) / (x[i] - x[i - j])
			self._data = (x, y)
			self.degree = n
			self._str = None

		def __call__(self, val):
			x, y = self._data
			n = self.degree
			sum = 0
			for i in range(n):
				prod = y[i]
				for j in range(i):
					prod *= val - x[j]
				sum += prod
			return sum

		def __str__(self):
			if self._str is None:
				x, y = self._data
				self._str = splice(
					"\n\t",
					"Lagrange polynomial (%d):",
					"x: %s",
					"y: %s") % (self.degree, x, y)

			return self._str

__all__ = ['Polynomial']

def test():
	kmax = 200

	#P = Polynomial(1, -6, 11, -6)
	seq = randSeq(20, -10, 10, 90, 5)
	P = Polynomial(seq)
	sol = P.solve(kmax)
	if sol:
		temp = []
		for r in sol:
			temp.append(str(round(r, 5)))
			temp.append('%f: ' % round(P(r), 5))

		#temp = sorted(set(temp))
		sol = ', '.join(temp)
	#print(temp)
	else:
		sol = 'None'

	with open('results.txt', 'a') as f:
		f.write('%s\n%s\n\n' % (P, sol))

x=[0,1,2,3]
y=[1,1,13,49]
P = Polynomial.Lagrange(x,y)
print(P(0.5)) #-1.25