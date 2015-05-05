"""
Prime numbers taken from [The Prime Pages](https://primes.utm.edu/lists/2small/200bit.html)
"""

from lib import post, crypto
from lib.math import mpz
from lib.ntheory import is_prime, next_prime, AdditionChain
from itertools import count
import time

################### Part 1 ###################
post(1)

#N = [mpz(2) ** 293 - k for k in [601, 1095, 1389]]
N = []; k = (mpz(1) << 1024) - 1
for i in range(3):
	k = next_prime(k)
	N.append(k)
del k
#E = [mpz(2) ** 145 - k for k in [151, 295]][:1]
E = [next_prime((mpz(1) << 512) - 1)]
for k in N[:3]:
	assert is_prime(k, 100)
post(1, 'p, q and r are all prime numbers')

RSA = crypto.RSA(N, crypto.RSA.Exp(E))
x = mpz(time.time())
T = [(k, RSA.test(k, x)) for k in RSA.dec]
post(2, 'all decryption results are correct')

################### Part 2 ###################
post(2)

Ds = ['d mod (%s - 1)' % k for k in 'pqr']
C = [AdditionChain(d) for d in RSA.D]
m = '%s has %d bits and a binary addition chain of %d'
for i, s, d, c in zip(count(1), Ds, RSA.D, C):
	post(i, m % (s, d.bit_length(), len(c)))

################### Part 3 ###################
post(3)

m = 'decrypt_%s took %sms, simple decryption took %sms, ratio: %d'
for i, t in zip(count(1), T):
	k, t = t
	td, te = t
	post(i, m % (k, td, te, round(te / td, 3)))