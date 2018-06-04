'use strict';

angular.module('agent_list')
	.component('myAgentList', {
		templateUrl: 'part/agent_list/agent_list.template.html',
		controller: function( $rootScope, $state,wsService,$scope,$element,agentService) {
			wsService.getAgentClasses();
			 var agentListVm = this;
			 
			$rootScope.agentClasses = [];
			
			$scope.$on('agentClasses', function (event, arg) { 
				
				$rootScope.agentClasses = arg.filter(function(el){
					return !el.agentClass.includes("Local");
				});
				$scope.$apply();
			  });
			
			this.send = (agentClass) =>{
				agentService.activateAgent(agentClass.agentClass,this.agentName,agentClass.host)
				.then( (response) => {
					alert('Agent added')
				}, () => {
					alert('Could not activate agent')
				});
				
			}
		}
	});		