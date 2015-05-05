from lib.math import *
from lib import post, hexa
from itertools import combinations
from libnum.common import randint_bits
from random import randint

size = 32
p = 11# 1019
m = 29# randint_bits(size) | (1 << (size - 1))
post(1, 'message m: %X, base p: %s', m, p)

s = 1
P = to_base(m, p)
k = P.degree + 1
n = k + 2*s
post(2, 'P: %s, k: %s, n: %s', P, k, n)

y = Vector((P(i, p) for i in range(1, n+1)), hexa)
post(3, 'vector y: %s', y)

d = 2#randint(1, n)
z = Vector([9, 2, 6, 5, 8], hexa)
#z = Vector((y[i] if i != d else (y[i] + 1) % p for i in range(n)), hexa)
post(4, 'vector z: %s, error index: %d', z, d)

Q = ()
T = (e for e in range(1, n+1) if e != d)
for A in combinations(T, k):
	B = lambda x: (e for e in A if e != x)
	frac = lambda i: ((j * invmod(j - i, p)) % p for j in B(i))
	fc = sum(((z[i-1] * prod(frac(i), p)) % p for i in A), p)
	if fc == 0:
		Q = Polynomial.lagrange(A, z, p)
		break

post(5, 'polynomial Q: %s', Q)