'use strict';

angular.module('agent-chat')
	.config(function($stateProvider, $urlRouterProvider) {
		$stateProvider
			.state({
				name: 'home',
				url: '/',
				component: 'myHome'
			})
			.state({
				name: 'home.userAuth',
				url: 'user-auth',
				component: 'myUserAuth'
			})
			.state({
				name: 'home.chat',
				url: 'chat',
				component: 'myChat'
			})
			.state({
				name: 'home.agent_list',
				url: 'agent_list',
				component: 'myAgentList'
			})
			.state({
				name: 'home.agents_active',
				url: 'agents_active',
				component: 'myAgentsActive'
			})
			.state({
				name: 'home.groups',
				url: 'groups',
				component: 'groupsComponent'
			})
//			.state({
//				name: 'home.chart',
//				url: '^/chart/:id?monthTime',
//				component: 'myChart'
//			})
//			.state({
//				name: 'home.places',
//				url: '^/places/{placeType:theater|cinema}',
//				component: 'myPlaceList'
//			})
//			
//			.state({
//				name: 'home.projectionForm',
//				url: '^/projectionForm/:idPlace?idProjection',
//				component: 'myProjectionForm'
//			})

		$urlRouterProvider
			.when('', '/')
			.otherwise('/error');
	})
	.run(function($rootScope, wsService) {
		$rootScope.wsService = wsService;
	});
