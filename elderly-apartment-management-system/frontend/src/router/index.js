import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getCurrentUser } from '@/api/auth'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录', public: true }
  },
  {
    path: '/',
    component: () => import('@/layout/index.vue'),
    redirect: '/dashboard',
    meta: { title: '首页', icon: 'HomeFilled' },
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '首页' }
      }
    ]
  },
  // 移动端布局路由 - 带有底部导航栏
  {
    path: '/mobile',
    component: () => import('@/layout/MobileLayout.vue'),
    redirect: '/mobile/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'MobileDashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '首页', icon: 'Odometer', mobile: true }
      },
      {
        path: 'activity',
        name: 'MobileActivity',
        component: () => import('@/views/activity/index.vue'),
        meta: { title: '活动', icon: 'Trophy', mobile: true }
      },
      {
        path: 'health',
        name: 'MobileHealth',
        component: () => import('@/views/health/index.vue'),
        meta: { title: '健康', icon: 'FirstAidKit', mobile: true }
      },
      {
        path: 'safety',
        name: 'MobileSafety',
        component: () => import('@/views/safety/index.vue'),
        meta: { title: '安全', icon: 'Shield', mobile: true }
      },
      {
        path: 'profile',
        name: 'MobileProfile',
        component: () => import('@/views/user/profile.vue'),
        meta: { title: '我的', icon: 'User', mobile: true }
      }
    ]
  },
  {
    path: '/elderly',
    component: () => import('@/layout/index.vue'),
    redirect: '/elderly/list',
    meta: { title: '住客管理', icon: 'User' },
    children: [
      {
        path: 'list',
        name: 'ElderlyList',
        component: () => import('@/views/elderly/list.vue'),
        meta: { title: '住客列表' }
      },
      {
        path: 'check-out/:id',
        name: 'ElderlyCheckOut',
        component: () => import('@/views/elderly/check-out.vue'),
        meta: { title: '住客退住' }
      },
      {
        path: 'family',
        name: 'FamilyManagement',
        component: () => import('@/views/family/index.vue'),
        meta: { title: '家属管理' }
      }
    ]
  },
  {
    path: '/room',
    component: () => import('@/layout/index.vue'),
    redirect: '/room/list',
    meta: { title: '房间管理', icon: 'House' },
    children: [
      {
        path: 'list',
        name: 'RoomList',
        component: () => import('@/views/room/list.vue'),
        meta: { title: '房间列表' }
      },
      {
        path: 'statistics',
        name: 'RoomStatistics',
        component: () => import('@/views/room/statistics.vue'),
        meta: { title: '房间统计' }
      },
      {
        path: 'service',
        name: 'RoomService',
        component: () => import('@/views/room/service.vue'),
        meta: { title: '房间服务' }
      }
    ]
  },
  {
    path: '/health',
    component: () => import('@/layout/index.vue'),
    redirect: '/health/index',
    meta: { title: '健康管理', icon: 'FirstAidKit' },
    children: [
      {
        path: 'index',
        name: 'HealthIndex',
        component: () => import('@/views/health/index.vue'),
        meta: { title: '健康档案' }
      },
      {
        path: 'record',
        name: 'HealthRecord',
        component: () => import('@/views/health/record-page.vue'),
        meta: { title: '健康记录' }
      },
      {
        path: 'report',
        name: 'HealthReport',
        component: () => import('@/views/health/report-page.vue'),
        meta: { title: '体检报告' }
      }
    ]
  },
  {
    path: '/care',
    component: () => import('@/layout/index.vue'),
    redirect: '/care/record',
    meta: { title: '护理管理', icon: 'FirstAidKit' },
    children: [
      {
        path: 'record',
        name: 'CareRecord',
        component: () => import('@/views/care/record.vue'),
        meta: { title: '护理记录' }
      },
      {
        path: 'booking',
        name: 'CareBooking',
        component: () => import('@/views/care/booking.vue'),
        meta: { title: '护理预约' }
      }
    ]
  },

  {
    path: '/activity',
    component: () => import('@/layout/index.vue'),
    redirect: '/activity/list',
    meta: { title: '活动管理', icon: 'Trophy' },
    children: [
      {
        path: 'list',
        name: 'ActivityList',
        component: () => import('@/views/activity/list.vue'),
        meta: { title: '活动列表' }
      },
      {
        path: 'signup/:id',
        name: 'ActivitySignup',
        component: () => import('@/views/activity/signup.vue'),
        meta: { title: '报名管理', hidden: true }
      },
      {
        path: 'feedback/:id',
        name: 'ActivityFeedback',
        component: () => import('@/views/activity/feedback.vue'),
        meta: { title: '活动反馈', hidden: true }
      },
      {
        path: 'category',
        name: 'ActivityCategory',
        component: () => import('@/views/activity/category.vue'),
        meta: { title: '分类管理' }
      }
    ]
  },
  {
    path: '/meal',
    component: () => import('@/layout/index.vue'),
    redirect: '/meal/menu',
    meta: { title: '餐饮管理', icon: 'Food' },
    children: [
      {
        path: 'menu',
        name: 'MealMenu',
        component: () => import('@/views/meal/menu.vue'),
        meta: { title: '菜品管理' }
      },

      {
        path: 'order',
        name: 'MealOrder',
        component: () => import('@/views/meal/order.vue'),
        meta: { title: '住客订餐' }
      }
    ]
  },
  {
    path: '/fee',
    component: () => import('@/layout/index.vue'),
    redirect: '/fee/list',
    meta: { title: '费用管理', icon: 'Money' },
    children: [
      {
        path: 'list',
        name: 'FeeList',
        component: () => import('@/views/fee/list.vue'),
        meta: { title: '费用账单' }
      },
      {
        path: 'payment',
        name: 'FeePayment',
        component: () => import('@/views/fee/payment.vue'),
        meta: { title: '缴费记录' }
      },
      {
        path: 'statistics',
        name: 'FeeStatistics',
        component: () => import('@/views/fee/statistics.vue'),
        meta: { title: '费用统计' }
      }
    ]
  },
  {
    path: '/system',
    component: () => import('@/layout/index.vue'),
    redirect: '/system/user',
    meta: { title: '系统管理', icon: 'Setting' },
    children: [
      {
        path: 'user',
        name: 'SystemUser',
        component: () => import('@/views/system/user.vue'),
        meta: { title: '用户管理' }
      },
      {
        path: 'role',
        name: 'SystemRole',
        component: () => import('@/views/user/role.vue'),
        meta: { title: '角色管理' }
      }
    ]
  },
  {
    path: '/emergency',
    component: () => import('@/layout/index.vue'),
    redirect: '/emergency/index',
    meta: { title: '紧急呼叫', icon: 'PhoneFilled' },
    children: [
      {
        path: 'index',
        name: 'EmergencyIndex',
        component: () => import('@/views/emergency/index.vue'),
        meta: { title: '紧急呼叫管理' }
      }
    ]
  },
  {
    path: '/user',
    component: () => import('@/layout/index.vue'),
    redirect: '/user/profile',
    meta: { title: '个人中心', icon: 'UserFilled' },
    children: [
      {
        path: 'profile',
        name: 'UserProfile',
        component: () => import('@/views/user/profile.vue'),
        meta: { title: '个人信息' }
      },
      {
        path: 'password',
        name: 'UserPassword',
        component: () => import('@/views/user/password.vue'),
        meta: { title: '修改密码' }
      }
    ]
  },
  {
    path: '/announcement',
    component: () => import('@/layout/index.vue'),
    redirect: '/announcement/index',
    meta: { title: '公告管理', icon: 'Bell' },
    children: [
      {
        path: 'index',
        name: 'AnnouncementIndex',
        component: () => import('@/views/announcement/index.vue'),
        meta: { title: '公告列表' }
      }
    ]
  },
  {
    path: '/banner',
    component: () => import('@/layout/index.vue'),
    redirect: '/banner/index',
    meta: { title: '轮播图管理', icon: 'Picture' },
    children: [
      {
        path: 'index',
        name: 'BannerIndex',
        component: () => import('@/views/banner/index.vue'),
        meta: { title: '轮播图列表' }
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/404.vue'),
    meta: { title: '404', public: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach(async (to, from, next) => {
  document.title = `${to.meta.title || '老年公寓管理系统'} - 老年公寓管理系统`
  
  const userStore = useUserStore()
  
  if (to.meta.public) {
    next()
    return
  }
  
  // 检查是否有token
  if (!userStore.token) {
    next({ name: 'Login', query: { redirect: to.fullPath } })
    return
  }
  
  // 如果有token但没有用户信息，尝试获取当前用户信息
  if (!userStore.user) {
    try {
      const res = await getCurrentUser()
      if (res.code === 200) {
        userStore.setUser(res.data)
        next()
      } else {
        // 获取用户信息失败，清除token并跳转到登录页
        await userStore.doLogout(false)
        next({ name: 'Login', query: { redirect: to.fullPath } })
      }
    } catch (error) {
      // 请求失败，清除token并跳转到登录页
      await userStore.doLogout(false)
      next({ name: 'Login', query: { redirect: to.fullPath } })
    }
    return
  }
  
  next()
})

export default router
