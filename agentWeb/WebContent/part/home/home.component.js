'use strict';

angular.module('home')
	.component('myHome', {
		templateUrl: 'part/home/home.template.html',
		controller: function( $scope, $rootScope) {
			this.console = "";
			var _this = this;
			$scope.$on('LOG', function (event, arg) {
				_this.console += arg + "\n";
				$scope.$apply();
			});
		}
	});		