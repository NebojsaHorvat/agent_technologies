'use strict';

angular.module('core.agent')
	.service('agentService', function($http) {
		
		this.activateAgent = (className,agentName,host) => {
			return $http.post('/agentWeb/rest/agents/running/'+className+'/'+agentName,host);
		};
		this.getActiveAgents = () => {
			return $http.get('/agentWeb/rest/agents/running');
		};
		this.stopAgent = (agent) => {
			return $http.post('/agentWeb/rest/agents/running/delete',agent.aid);
		};
	});