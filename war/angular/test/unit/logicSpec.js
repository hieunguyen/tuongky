'use strict';

describe('Logic classes:', function() {

	describe('Params class', function() {

		it('should construct from object correctly.', function() {
			var params = new Params({
				q: 'Hello',
				start: 10
			})
			expect(params.encode()).toBe('q=Hello&start=10');
			params.set('advanced', true);
  		expect(params.encode()).toBe('q=Hello&start=10&advanced=true');
  		params.clear();
    	expect(params.encode()).toBe('');
		});

		it('should construct from string correctly.', function() {
			var params = new Params('q=Hello+World&start=10');
			expect(params.get('q')).toBe('Hello World');
			expect(params.get('start')).toBe('10');

			params = new Params('q=Hello%20World&start=10');
			expect(params.get('q')).toBe('Hello World');

			params = new Params('q=Hứa+Ngân+Xuyên&start=10');
			expect(params.get('q')).toBe('Hứa Ngân Xuyên');

			params = new Params('q=Hứa%20Ngân+Xuyên&start=10');
			expect(params.get('q')).toBe('Hứa Ngân Xuyên');

			params.set('start', 20);
			expect(params.encode()).toBe(
				  'q=H%E1%BB%A9a+Ng%C3%A2n+Xuy%C3%AAn&start=20');
		});
	});
});
