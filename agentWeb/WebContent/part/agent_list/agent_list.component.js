'use strict';

angular.module('agent_list')
	.component('myAgentList', {
		templateUrl: 'part/agent_list/agent_list.template.html',
		controller: function( $rootScope, $state,wsService,$scope,$element,agentService) {
			
			wsService.getAgentClasses();
			 var agentListVm = this;
			 
			$rootScope.agentClasses = ['a','b','c'];
			
			$scope.$on('agentClasses', function (event, arg) { 
				
				$rootScope.agentClasses = arg.filter(function(el){
					return !el.includes("Local");
				});
				$scope.$apply();
			  });
			
			this.send = (className) =>{
				agentService.activateAgent(className,this.agentName)
				.then( (response) => {
					alert('Agent added')
				}, () => {
					alert('Could not activate agent')
				});
				
			}
		}
	});		