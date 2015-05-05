import tokenize as _tok
import inspect as _inspect
from helpers import stamp

def _name(val):
	return [_tok.NAME, val]

def _oper(val):
	return [_tok.OP, val]

_newline = [_tok.NEWLINE, '\n']

def sel(obj):
	if isinstance(obj, type):
		return obj
	if not type(obj) is type(sel):
		return None
	func = obj
	obj = _inspect.getsourcelines(func)[0][1:]
	readline = iter(obj).__next__
	src = _tok.generate_tokens(readline)
	new = [[_tok.ENCODING, 'utf-8']]
	cond, temp = None, None
	alias = False

	for tok in src:
		tType, tVal = tok[:2]
		isTok = lambda val, _type_: tVal == val and tType == _type_
		isName = lambda val: isTok(val, _tok.NAME)
		isOper = lambda val: isTok(val, _tok.OP)

		if cond:
			if tType == _tok.INDENT:
				indent += 1
				if alias:
					new.append(tok)
					new.extend(alias)
					alias = None
					continue

			elif tType == _tok.DEDENT:
				indent -= 1
				if indent <= 0:
					cond = None

				if indent <= 1:
					new.extend([_name('break'), _newline])

		if isName('with'):
			size = None
			if cond and indent <= 1:
				if indent <= 0:
					cond = None

				else:
					temp = []

			if not cond:
				temp = []
				var = None

		if temp is None:
			new.append(tok)

		else:
			if isName('_') and not cond:
				cond = True

			elif isName('as'):
				size = len(temp)

			elif isOper(':'):
				if cond is None:
					temp.append(tok)

				else:
					if size:
						alias = temp[size + 1:]
						temp = temp[1:size]

					else:
						temp = temp[1:]

					if cond is True:
						indent = 0
						cond = temp
						temp = [_name('while'), _name('True'), tok]
						if size:
							var = alias
							alias = None

					else:	#elif isinstance(cond, list):
						temp = [_oper('[')] + temp + [_oper(e) for e in [']', ')', tVal]]
						temp = cond + [_name(e) for e in ['for', '_', 'in']] + temp
						temp = [_name('if'), _name('any'), _oper('(')] + temp

						if size and var:
							alias += [_oper('=')] + var + [_newline]

						else:
							alias = None

				new.extend(temp)
				temp = None
				continue

			temp.append(tok)

	new = _tok.untokenize(new).decode('utf-8')
	#stamp(new)
	new = compile(new, _inspect.getfile(func), 'exec')
	temp = {}
	exec(new, func.__globals__, temp)
	return temp[func.__name__]

__all__ = ['sel']

@sel
def test(x):
	print('A')
	with open('results.txt'):
		print('B')
	print('C')
	with x == _ as x:
		with 1:
			print('one')
		with 1, 2:
			print('two')
		print('else: %s'%x)
	print('D')

def _test():
	test(1)
	test(2)
	test(0)
