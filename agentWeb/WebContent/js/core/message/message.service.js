'use strict';

angular.module('core.message')
	.service('messageService', function($http) {
		this.getPerformatives = () => {
			return $http.get('/agentWeb/rest/messages');
		};
		this.sendMessage = (data) => {
			return $http.post('/agentWeb/rest/messages',data);
		};
	});