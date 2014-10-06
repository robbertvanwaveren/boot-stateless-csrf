var app = angular.module('statelessApp', []);

app.config(['$httpProvider', function($httpProvider) {
	//fancy random token
	function b(a){return a?(a^Math.random()*16>>a/4).toString(16):([1e16]+1e16).replace(/[01]/g,b)}; 
	
	$httpProvider.interceptors.push(function() {
		return {
			'request': function(config) {
				// put a new random secret into our CSRF-TOKEN Cookie after each response
				document.cookie = 'CSRF-TOKEN=' + b();
				return config;
			}
		};
	});	
}]);

app.controller('CsrfCtrl', function ($scope, $http) {
	$scope.result = "";
	
	$scope.init = function () {
		$http.defaults.xsrfHeaderName = 'X-CSRF-TOKEN';
		$http.defaults.xsrfCookieName = 'CSRF-TOKEN';
	};

	$scope.testPost = function () {
		$http.post('/api/test').success(function (result) {
			$scope.result = result;
		});
	};
});