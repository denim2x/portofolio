# coding=utf-8
from .math import prod
from .ntheory import gcd
from contexttimer import Timer

class RSA:
	"""
	Provides efficient implementations of various
	algorithms related to the RSA cryptosystem.
	"""

	def __init__(self, N, gen_e):
		p, q, r = N
		def exp(Tn):
			tn = prod(Tn)
			e, iter_e = tn, iter(gen_e)
			while gcd(tn, e) != 1:
				e = next(iter_e)
			d = e ** (-1, tn); print d.bit_length()
			D = [d % m for m in Tn]
			return e, d, D

		pq, p_q = p * q, p ** (-1, q)
		pqr, pq_r = pq * r, pq ** (-1, r)
		Tn = [k - 1 for k in N]
		e, d, D = exp(Tn); self.D = D
		self._pqr = (N, D, range(3))
		self._g = (pqr, p, q, r, pq, p_q, pq_r)

		pqr, p2 = zip([d, e], [pqr] * 2), p ** 2
		p2q, p2_q = p2 * q, p2 ** (-1, q)
		Tn = [p*(p-1),q-1]
		e, d, D = exp(Tn)
		e_p = e ** (-1, p)
		dp, dq = D
		self._p2q = (p2q, p2, q, dq, p2_q)
		self._h = (p, p2, dp, e, e_p)

		p2q = zip([d, e], [p2q] * 2)
		names, N = ('pqr', 'p2q'), (pqr, p2q)
		self.dec, self.enc = (dict(zip(names,
		    [k[i] for k in N])) for i in range(2))

	def decrypt_pqr(self, y):
		"""Multi-prime decryption"""
		N, D, I = self._pqr
		Y = [y % k for k in N]
		M = (Y[i] ** (D[i], N[i]) for i in I)
		return self._garner(M)

	def _garner(self, M):
		"""Garner’s algorithm for n = pqr"""
		n, p, q, r, pq, p_q, pq_r = self._g
		mp, mq, mr = M
		m = ((mq - mp) % q * p_q) % q
		mpq = ((m * p) % pq + mp) % pq
		m = ((mr - mpq) % r * pq_r) % r
		m = ((m * p) % n * q) % n
		return (m + mpq) % n

	def decrypt_p2q(self, y):
		"""Multi-factor decryption"""
		n, p2, q, dq, p2_q = self._p2q
		yq, mp = y % q, self._hensel(y)
		k = (yq ** (dq, q) - mp) % q
		m = ((k * p2_q) % q * p2) % n
		return (m + mp) % n

	def _hensel(self, y):
		"""Improved Hensel lifting algorithm"""
		p, p2, dp, e, e_p = self._h
		yp = y % p2
		mp = yp ** (dp - 1, p)
		k = (mp * yp) % p
		a = ((-k) ** (e, p2) + y) % p2
		mp = ((mp * a) % p2 * e_p) % p2
		return (mp + k) % p2

	def test(self, name, x):
		y = self.encrypt(name, x)
		args = {'factor': 1e3}
		with Timer(**args) as td:
			xd = self.decrypt(name, y)
		assert x == xd
		with Timer(**args) as te:
			xe = y ** self.dec[name]
		assert x == xe
		#assert x == xe or name == 'p2q'
		return [round(t.elapsed, 3) for t in (td, te)]

	def __hensel(self, y):
		p, p2, dp, e, e_p = self._h
		yp = y % p2
		k = yp ** (dp, p)
		a = -k ** (e, p2)
		a = (a + y) % p2
		a = a / p
		t = k ** (e-1, p)
		t = (t * e) % p
		t = t ** (-1, p)
		t = (t * a) % p
		mp = (p * t) % p2
		return (mp + k) % p2

	def decrypt(self, name, y):
		return getattr(self, 'decrypt_' + name)(y)

	def encrypt(self, name, x):
		y = x ** self.enc[name]
		return y

	class Exp:
		def __init__(self, seq):
			self._seq = seq
			self._i = 0
			self._len = len(seq)

		def __iter__(self):
			i = self._i
			self._i = (i + 1) % self._len
			return iter([self._seq[i]])