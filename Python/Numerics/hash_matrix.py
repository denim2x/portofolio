#!/usr/bin/python
from numbers import Integral
from collections.abc import Iterable
from helpers import enum, epsilon, sign
from itertools import count
from selective import *

class HashMatrix:
	def __init__(self, arg, cols=None):
		self._data = {}
		if isinstance(arg, Integral):
			rows = arg
		elif isinstance(arg, Iterable):
			m = {}
			it = iter(arg).__next__
			for i in count():
				try:
					for j in range(cols):
						m[i, j] = it()
				except StopIteration:
					rows = i
					break
			self._data = m
		self.rows = rows
		self.cols = rows if cols is None else cols
		self._len = rows * self.cols

	def __getitem__(self, key):
		if isinstance(key, Integral):
			key = (key, key)
		data = self._data
		return data[key] if key in data else None

	def __setitem__(self, key, value):
		if isinstance(key, slice):
			row, col = key.start, key.stop
			if row is not None:
				key = lambda c: (row, c)
			elif col is not None:
				key = lambda r: (r, col)
			else:
				key = None
			if key:
				n = len(value)
				for i in range(n):
					self._data[key(i)] = value[i]
			return
		if isinstance(key, Integral):
			key = (key, key)
		self._data[key] = value

	@sel
	def __mul__(self, m):
		with isinstance(m, _):
			with HashMatrix:
				r = HashMatrix(self.rows, m.cols)
				for i in range(self.rows):
					for j in range(self.cols):
						r[i, j] = sum(self[i, k] * m[k, j]
									  for k in range(self.cols))
			with Iterable:
				r = HashMatrix(self.rows, 1)
				for i in range(self.rows):
					r[i, 0] = sum(self[i, j] * m[j]
								  for j in range(self.cols))
		return r

	def __sub__(self, m):
		r = HashMatrix(self.rows, self.cols)
		for i in range(self.rows):
			for j in range(self.cols):
				r[i, j] = self[i, j] - m[i, j]
		return r

	def __rmul__(self, x):
		m = self.clone()
		for i in range(self.rows):
			for j in range(self.cols):
				m[i, j] *= x
		return m

	def qr(self, B=None):
		a = self
		n = a.rows
		I = HashMatrix.ident(n)
		q = I
		for r in range(n - 1):
			s = sum(a[i, r] ** 2 for i in range(r, n))
			if s <= epsilon:
				break
			k = -sign(a[r]) * s ** 0.5
			b = s - k * a[r]
			u = [0] * r + [a[r] - k] + \
				[a[i, r] for i in range(r + 1, n)]
			u = HashMatrix.outer(u, u)
			P = I - b ** -1 * u
			a = P * a
			q = P * q
		if B is None:
			return q.trans(), a
		else:
			return a, q * B
		return q.trans(), a if B is None \
			else a, q * B

	def trans(self):
		m = HashMatrix(self.rows, self.cols)
		for i in range(self.rows):
			for j in range(self.cols):
				m[i, j] = self[j, i]
		return m

	def round(self, n):
		m = HashMatrix(self.rows, self.cols)
		m._data = {}
		self = self._data
		for k in self.keys():
			m._data[k] = round(self[k], n)
		return m

	@staticmethod
	def ident(size):
		m = HashMatrix([0] * size ** 2, size)
		m.diagonal = [1] * size
		return m

	@staticmethod
	def outer(a, b):
		r, c = len(a), len(b)
		m = HashMatrix(r, c)
		for i in range(r):
			for j in range(c):
				m[i, j] = a[i] * b[j]
		return m

	def __len__(self):
		return self._len

	@property
	def diagonal(self):
		for i in range(self.rows):
			yield self[i, i]

	@diagonal.setter
	def diagonal(self, value):
		n = len(value)
		for i in range(n):
			self._data[i, i] = value[i]

	def __str__(self):
		temp = []
		for i in range(self.rows):
			for j in range(self.cols):
				val = self[i, j]
				if val is None:
					val = 'NaN'
				temp.append('%10s' % round(val, 3))
			temp.append('\n')
		return ''.join(temp)

	def addCol(self, seq):
		m = self.clone()
		n = self.cols
		for i, e in enum(seq):
			m[i, n] = e
		m.cols = n + 1
		return m

	def clone(self):
		m = HashMatrix(self.rows, self.cols)
		m._data = dict(self._data)
		return m

	def jag(self):
		m = self._data
		rows = []
		cols = self.cols
		for i in range(self.rows):
			row = []
			for j in range(cols):
				row.append(m[i, j])
			rows.append(row)
		return rows

	@staticmethod
	def unjag(seq):
		rows = len(seq)
		cols = len(next(iter(seq)))
		m = HashMatrix(rows, cols)
		for i, row in enum(seq):
			for j, e in enum(row):
				m[i, j] = e
		return m

__all__ = ['HashMatrix']

def test():
	m = range(4)
	m = HashMatrix(m, 2)
	m = m.jag()
	print(m)
	m = HashMatrix.unjag(m)
	print(m._data)

A = [4, -3, -7,
	 3, 4, 1,
	 0, 0, 5]
A = HashMatrix(A, 3)
B = [1, 7, 0]
R, Q = A.qr(B)
print(A)
print('%s\n%s' % (R, Q))
exit()
Q, R = A.qr()
print('%s\n%s' % (Q, R))