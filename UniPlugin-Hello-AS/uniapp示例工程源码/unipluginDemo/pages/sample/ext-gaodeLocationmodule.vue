<template>
	<div>
		<button type="primary" @click="initGaode">初始化高德</button>
		<button type="primary" @click="startGaode">开始定位</button>
		<button type="primary" @click="stopGaode">结束定位</button>
		<button type="primary" @click="startBackgroundGaode">开启后台定位</button>
		<button type="primary" @click="stopBackgroundGaode">关闭后台定位</button>

	</div>
</template>

<script>
	// 获取 module 
	var gaodeLocation = uni.requireNativePlugin("leehor-gaode-location")
	const modal = uni.requireNativePlugin('modal');
	export default {
		onLoad() {
			plus.globalEvent.addEventListener('gaodeLocation', function(e){
				modal.toast({
					message: "定位信息：成功"+e.isSuccess+";经纬度lnglat:"+e.lngLat+";信息:"+e.location.errorCode,
					duration: 1.5
				});
			});
		},
		// onShow() {
		// 	this.stopBackgroundGaode()
		// },
		// onHide() {
		// 	this.startBackgroundGaode()
		// },
		beforeDestroy() {
			this.destroyLocationGaode()
		},
		methods: {
			initGaode() {
				// 调用异步方法
				gaodeLocation.initLocation({
						'interval': 2000,
					},
					(ret) => {
						modal.toast({
							message: ret,
							duration: 1.5
						});
					})
			},
			startGaode() {
				// 调用异步方法
				gaodeLocation.startLocation({
						
					},
					(ret) => {
						modal.toast({
							message: ret,
							duration: 1.5
						});
					})
			},
			stopGaode() {
				// 调用异步方法
				gaodeLocation.stopLocation({
						
					},
					(ret) => {
						modal.toast({
							message: ret,
							duration: 1.5
						});
					})
			},
			startBackgroundGaode() {
				// 调用异步方法
				gaodeLocation.startBackgroundLocation({
						
					},
					(ret) => {
						modal.toast({
							message: ret,
							duration: 1.5
						});
					})
			},
			stopBackgroundGaode() {
				// 调用异步方法
				gaodeLocation.stopBackgroundLocation({},
					(ret) => {
						modal.toast({
							message: ret,
							duration: 1.5
						});
					})
			},
			destroyLocationGaode() {
				// 调用异步方法
				gaodeLocation.destroyLocation({},
					(ret) => {
						modal.toast({
							message: ret,
							duration: 1.5
						});
					})
			},
		}
	}
</script>