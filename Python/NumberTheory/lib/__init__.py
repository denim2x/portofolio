from prelude import main, write, writeln
import platform

hexa = lambda n: '%X' % n
_div = '*' * 20
_part = 'lcyan white lcyan'
_post = 'cyan lcyan cyan'

def post(i, msg=None, *args):
	if msg is None:
		writeln(['\n'+_div, ' Part %d ' % i, _div], _part)
	else:
		write(['[', i, '] '], _post)
		print msg % args if len(args) > 0 else msg

write(
	['*** Running ', main, ' in ', platform.Python, ' ***\n'],
	'lcyan white lcyan lgrey lcyan'
)