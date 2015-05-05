#!/usr/bin/python
from hash_matrix import HashMatrix
from math import ceil, sin, cos
from linear_system import LinearSystem
from helpers import splice

def phi(i, x):
	if i == 0:
		return 1
	k = ceil(i / 2)
	return cos(k * x) \
		if i % 2 == 0 \
		else sin(k * x)

class LinearTrig:
	def __init__0(self, _map):
		n = len(_map)
		x = list(_map.keys())
		y = list(_map.values())

	def __init__(self, x, y):
		n = len(x)
		T = HashMatrix(n)
		for i in range(n):
			T[i, 0] = 1
			e = x[i]
			for j in range(1, n):
				T[i, j] = phi(j, e)

		S = LinearSystem(T, y)
		y = S.solve()
		self._data = (x, y)
		self.degree = n
		self._str = None

	def __call__(self, val):
		x, y = self._data
		if y is None:
			return None
		n = self.degree
		sum = y[0]
		for i in range(1, n):
			sum += y[i] * phi(i, x[i])
		return sum

	def __str__(self):
		if self._str is None:
			x, y = self._data
			self._str = splice(
				"\n\t",
				"Linear trigo (%d):",
				"x: %s",
				"y: %s") % (self.degree, x, y)

		return self._str