#!/usr/bin/python
from helpers import randSeq

from math import sin, cos, pi
from polynomial import Polynomial
from linear_trig import LinearTrig

def interpolation():
	x0, xn = 1, 5
	n = 5
	X = randSeq(n, x0, xn, 0, 5)
	Xf = sorted(X)

	x0, xn = 0, 31 * pi / 16
	X = randSeq(n, x0, xn, 0, 5)
	Xg = sorted(X)

	def f(x):
		return x ** 4 - 12 * x ** 3 + 30 * x ** 2 + 12

	def g(x):
		return sin(x) - cos(x)

	Yf = list(map(f, Xf))
	L = Polynomial.Lagrange(Xf, Yf)

	Yg = list(map(g, Xg))
	T = LinearTrig(Xg, Yg)

	x = -5
	print(L)
	print(abs(L(x) - f(x)))

	x = -pi
	print(T)
	print(abs(T(x) - g(x)))