'use strict';

angular.module('core.agent')
	.service('agentService', function($http) {
		
		this.activateAgent = (className,agentName) => {
			return $http.post('/agentWeb/rest/agents/running/'+className+'/'+agentName);
		};
	});