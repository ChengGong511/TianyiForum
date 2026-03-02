<template>
  <div class="dashboard">
    <el-skeleton v-if="loading" :rows="3" animated />
    <el-row v-else :gutter="16" class="stats-row">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <el-statistic title="总用户数" :value="stats.totalUsers">
            <template #prefix>
              <el-icon>
                <User />
              </el-icon>
            </template>
          </el-statistic>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <el-statistic title="今日新帖" :value="stats.todayPosts">
            <template #prefix>
              <el-icon>
                <Document />
              </el-icon>
            </template>
          </el-statistic>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <el-statistic title="待审投诉" :value="stats.pendingComplaints">
            <template #prefix>
              <el-icon>
                <Warning />
              </el-icon>
            </template>
          </el-statistic>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <el-statistic title="总浏览量" :value="stats.totalViews">
            <template #prefix>
              <el-icon>
                <View />
              </el-icon>
            </template>
          </el-statistic>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" style="margin-top: 16px">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>用户与帖子统计</span>
            </div>
          </template>
          <div ref="userPostChartRef" style="width: 100%; height: 300px"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>数据概览</span>
            </div>
          </template>
          <div ref="overviewChartRef" style="width: 100%; height: 300px"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref, nextTick } from 'vue'
import { User, Document, Warning, View } from '@element-plus/icons-vue'
import { getDashboardStats } from '@/api/admin/dashboard'
import * as echarts from 'echarts'
import type { ECharts } from 'echarts'

const stats = reactive({
  totalUsers: 0,
  totalPosts: 0,
  todayPosts: 0,
  pendingComplaints: 0,
  totalViews: 0,
})
const loading = ref(true)
const userPostChartRef = ref<HTMLElement>()
const overviewChartRef = ref<HTMLElement>()
let userPostChart: ECharts | null = null
let overviewChart: ECharts | null = null

const initCharts = () => {
  nextTick(() => {
    // 用户与帖子柱状图
    if (userPostChartRef.value) {
      userPostChart = echarts.init(userPostChartRef.value)
      userPostChart.setOption({
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow',
          },
        },
        legend: {
          data: ['数量'],
        },
        xAxis: {
          type: 'category',
          data: ['总用户数', '总帖子数', '今日新帖'],
        },
        yAxis: {
          type: 'value',
        },
        series: [
          {
            name: '数量',
            type: 'bar',
            data: [stats.totalUsers, stats.totalPosts, stats.todayPosts],
            itemStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: '#a8dafc' },
                { offset: 0.5, color: '#6eb6ff' },
                { offset: 1, color: '#93c9ff' },
              ]),
            },
            emphasis: {
              itemStyle: {
                color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                  { offset: 0, color: '#6eb6ff' },
                  { offset: 0.7, color: '#8ac5ff' },
                  { offset: 1, color: '#b3d9ff' },
                ]),
              },
            },
          },
        ],
      })
    }

    // 数据概览饼图
    if (overviewChartRef.value) {
      overviewChart = echarts.init(overviewChartRef.value)
      overviewChart.setOption({
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b}: {c} ({d}%)',
        },
        legend: {
          orient: 'vertical',
          left: 'left',
        },
        series: [
          {
            name: '数据统计',
            type: 'pie',
            radius: '50%',
            data: [
              { value: stats.totalViews, name: '总浏览量' },
              { value: stats.todayPosts, name: '今日新帖' },
              { value: stats.pendingComplaints, name: '待审投诉' },
            ],
            color: ['#91cc75', '#fac858', '#73c0de'],
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.2)',
              },
            },
          },
        ],
      })
    }
  })
}

onMounted(async () => {
  try {
    const data = await getDashboardStats()
    Object.assign(stats, data)
    initCharts()
  } catch (e) {
    console.error('加载数据失败', e)
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.dashboard {
  padding: 0;
}

.stats-row {
  margin-bottom: 16px;
}

.stat-card {
  text-align: center;
}

.card-header {
  font-weight: 600;
}
</style>
