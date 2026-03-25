<template>
  <div class="mobile-layout">
    <div class="mobile-content">
      <router-view v-slot="{ Component }">
        <transition name="slide-fade" mode="out-in">
          <keep-alive>
            <component :is="Component" />
          </keep-alive>
        </transition>
      </router-view>
    </div>
  </div>
</template>

<script setup>
</script>

<style lang="scss" scoped>
.mobile-layout {
  width: 100%;
  min-height: 100vh;
  background-color: #f5f7fa;

  .mobile-content {
      width: 100%;
      min-height: 100vh;
      padding: 0;
      box-sizing: border-box;
    }
}

// 页面切换动画
.slide-fade-enter-active,
.slide-fade-leave-active {
  transition: all 0.2s ease;
}

.slide-fade-enter-from {
  opacity: 0;
  transform: translateX(10px);
}

.slide-fade-leave-to {
  opacity: 0;
  transform: translateX(-10px);
}
</style>
