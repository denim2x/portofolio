#!/usr/bin/python
from helpers import epsilon

class LinearSystem:
	def __init__(self, A, b):
		self._data = (A.clone(), b)

	def solve(self):
		A, b = self._data
		n = len(b)
		A = A.addCol(b)

		for i in range(0, n):
			# Search for maximum in this column
			maxEl = abs(A[i, i])
			maxRow = i
			for k in range(i + 1, n):
				if abs(A[k, i]) > maxEl:
					maxEl = abs(A[k, i])
					maxRow = k

			# Swap maximum row with current row (column by column)
			for k in range(i, n + 1):
				tmp = A[maxRow, k]
				A[maxRow, k] = A[i, k]
				A[i, k] = tmp

			# Make all rows below this one 0 in current column
			for k in range(i + 1, n):
				if abs(A[i, i]) <= epsilon:
					return None
				c = -A[k, i] / A[i, i]
				for j in range(i, n + 1):
					if i == j:
						A[k, j] = 0
					else:
						A[k, j] += c * A[i, j]

		# Solve equation Ax=b for an upper triangular matrix A
		x = [0 for i in range(n)]
		for i in range(n - 1, -1, -1):
			x[i] = A[i, n] / A[i, i]
			for k in range(i - 1, -1, -1):
				A[k, n] -= A[k, i] * x[i]
		return x

__all__ = ['LinearSystem']

def test():
	from hash_matrix import HashMatrix
	from helpers import stamp

	a = [1, 2, -1, 2, -1, 1, -1, 1, 2]
	A = HashMatrix(a, 3)
	b = [1, 2, 1]
	S = LinearSystem(A, b)
	x = S.solve()
	print(x)