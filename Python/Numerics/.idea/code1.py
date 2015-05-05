
	try:
		new = _tok.untokenize(new).decode('utf-8')
	except IndexError as e:
		if str(e) == 'pop from empty list':
			print(e)
			for i in range(-1, -3, -1):
				if new[i][0] == _tok.DEDENT:
					new[i] = [_tok.ENDMARKER, '']
					break
			new = _tok.untokenize(new).decode('utf-8')
		else:
			raise e

			
	func = __FUNC__
	for e in dir(func):
		if e in ['__class__', '__closure__', '__globals__']:
			continue
		val = getattr(func, e)
	#setattr(new, e, val)
	return new
	
	
		body = string('%s %dx^%d' % (e, n - i, '+ ' if e > 0 else '')
					  if n - i > 1 else
					  ( '%dx' % e if n - i == 1  else str(e))
					  for i, e in enum(self) if not zero(e))