<template>
  <el-container class="layout-container">
    <el-aside :width="collapsed ? '64px' : '220px'" class="layout-aside" :class="{ 'is-collapsed': collapsed }">
      <div class="logo">
        <span v-show="collapsed" class="logo-short">老年公寓</span>
        <span v-show="!collapsed" class="logo-full">老年公寓管理系统</span>
      </div>
      <el-scrollbar>
        <el-menu
          :default-active="activeMenu"
          :collapse="collapsed"
          :collapse-transition="true"
          router
          background-color="#304156"
          text-color="#bfcbd9"
          active-text-color="#409EFF"
          class="health-menu"
          @select="handleMenuSelect"
        >
          <template v-for="route in menuRoutes" :key="route.path">
            <el-sub-menu
              v-if="route.children && route.children.length > 1"
              :index="route.path"
              class="sub-menu-item"
            >
              <template #title>
                <el-icon><component :is="route.meta.icon" /></el-icon>
                <span class="menu-title">{{ route.meta.title }}</span>
              </template>
              <el-menu-item
                v-for="child in route.children"
                :key="child.path"
                :index="`${route.path}/${child.path}`.replace(/\/+\//g, '/')"
                class="sub-menu-content"
              >
                <el-icon><component :is="getChildIcon(child.meta.title)" /></el-icon>
                <span>{{ child.meta.title }}</span>
              </el-menu-item>
            </el-sub-menu>
            <el-menu-item
              v-else-if="route.children && route.children.length === 1"
              :index="`${route.path}/${route.children[0].path}`.replace(/\/+\//g, '/')"
            >
              <el-icon v-if="route.meta.icon"><component :is="route.meta.icon" /></el-icon>
              <template #title>{{ route.children[0].meta.title }}</template>
            </el-menu-item>
          </template>
        </el-menu>
      </el-scrollbar>
    </el-aside>
    <el-container class="layout-container">
      <el-header class="layout-header">
        <div class="header-left">
          <el-icon class="collapse-btn" @click="toggleSidebar">
            <component :is="collapsed ? 'Expand' : 'Fold'" />
          </el-icon>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item v-for="item in breadcrumbs" :key="item.path">
              {{ item.meta.title }}
            </el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-dropdown trigger="click" @command="handleCommand">
            <div class="user-info">
              <el-avatar :size="32" :src="userStore.user?.avatar">
                <el-icon><User /></el-icon>
              </el-avatar>
              <span class="username">{{ userStore.user?.realName || userStore.user?.username }}</span>
              <el-icon class="dropdown-icon"><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">
                  <el-icon><User /></el-icon>
                  个人中心
                </el-dropdown-item>
                <el-dropdown-item command="password">
                  <el-icon><Lock /></el-icon>
                  修改密码
                </el-dropdown-item>
                <el-dropdown-item divided command="logout">
                  <el-icon><SwitchButton /></el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      <el-main class="layout-main">
        <router-view v-slot="{ Component }">
          <transition name="slide-fade" mode="out-in">
            <keep-alive>
              <component :is="Component" />
            </keep-alive>
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAppStore, useUserStore } from '@/stores'
import { ElMessageBox, ElMessage } from 'element-plus'
import request from '@/api/request'

const route = useRoute()
const router = useRouter()
const appStore = useAppStore()
const userStore = useUserStore()

const collapsed = computed(() => appStore.sidebarCollapsed)

const toggleSidebar = () => {
  appStore.toggleSidebar()
}

const activeMenu = computed(() => route.path)

const handleMenuSelect = async (index) => {
  try {
    const response = await request.post('/menu/click', { path: index })
    if (response.success) {
      console.log('菜单点击记录成功:', index)
    }
  } catch (error) {
    console.error('菜单点击记录失败:', error)
  }
}

const menuRoutes = computed(() => {
  return router.getRoutes().filter(r => r.meta && r.meta.title && !r.meta.public && r.children && r.children.length > 0)
    .map(route => ({
      ...route,
      children: route.children.filter(child => !child.meta?.hidden)
    }))
    .filter(route => route.children.length > 0)
})

const breadcrumbs = computed(() => {
  return route.matched.filter(r => r.meta && r.meta.title)
})

const handleCommand = async (command) => {
  switch (command) {
    case 'profile':
      router.push('/user/profile')
      break
    case 'password':
      router.push('/user/password')
      break
    case 'logout':
      try {
        await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        await userStore.doLogout()
      } catch (e) {
        // Cancel
      }
      break
  }
}

const getMenuIndex = (route, child) => {
  const basePath = route.path.replace(/\/$/, '')
  const childPath = child.path.replace(/^\//, '')
  const fullPath = `${basePath}/${childPath}`.replace(/\/+\//g, '/')
  return fullPath
}

const getChildIcon = (title) => {
  const iconMap = {
    '健康档案': 'Document',
    '健康记录': 'DataAnalysis',
    '体检报告': 'Files',
    '个人信息': 'User',
    '修改密码': 'Lock',
    '房间管理': 'House',
    '住客管理': 'UserFilled',
    '护工管理': 'Avatar',
    '菜品管理': 'Dish',
    '住客订餐': 'EditPen',
    '送餐记录': 'Checked',
    '访客管理': 'Bell',
    '系统设置': 'Setting',
    '紧急呼叫管理': 'PhoneFilled',
    '用户管理': 'User',
    '角色管理': 'UserFilled',
    '权限管理': 'Key'
  }
  return iconMap[title] || 'Menu'
}
</script>

<style lang="scss" scoped>
.layout-container {
  width: 100%;
  height: 100vh;
}

.layout-aside {
  background-color: #304156;
  transition: width 0.3s;
  overflow: hidden;

  .logo {
    height: 64px;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 12px;
    background-color: #263445;
    color: #fff;
    font-size: 16px;
    font-weight: 600;
    overflow: hidden;

    &.collapsed {
      padding: 0;
      justify-content: center;
    }

    img {
      width: 32px;
      height: 32px;
    }
  }

  :deep(.el-menu) {
    border-right: none;
  }
}

.layout-header {
  background-color: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;

  .header-left {
    display: flex;
    align-items: center;
    gap: 16px;

    .collapse-btn {
      font-size: 20px;
      cursor: pointer;
      color: #606266;

      &:hover {
        color: #409EFF;
      }
    }
  }

  .header-right {
    display: flex;
    align-items: center;
    gap: 16px;

    .user-info {
      display: flex;
      align-items: center;
      gap: 8px;
      cursor: pointer;
      padding: 4px 8px;
      border-radius: 4px;

      &:hover {
        background-color: #f5f7fa;
      }

      .username {
        font-size: 14px;
        color: #606266;
      }

      .dropdown-icon {
        font-size: 12px;
        color: #909399;
      }
    }
  }
}

.layout-main {
  background-color: #f5f7fa;
  padding: 20px;
  overflow-y: auto;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
