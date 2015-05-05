from lib.ntheory import next_prime, SolovayStrassen as sos
from lib.math import mpz

n = (mpz(1) << 1023) - 1
t = 1
q = 100
k = q
for i in range(q):
	n = next_prime(n) - 1
	k -= int(sos.test(n, t))

print 'Accuracy: %.2f%%' % round(k * 100 / q, 3)