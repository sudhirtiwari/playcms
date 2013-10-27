define([],
function() {
    describe('A Test Suite', function() {
        it('should load some html', function() {
            document.body.innerHTML = __html__['test/e2e/fixture/sample.html'];
            expect(document.getElementById('tpl')).toBeDefined();
        });
    });
});
