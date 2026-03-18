<script setup lang="ts">
import { computed } from 'vue'
import { useData } from 'vitepress'
import SiteIcon from './SiteIcon.vue'
import { getSiteIconName } from '../utils/site-icons'

const { frontmatter, page } = useData()

const title = computed(() =>
  typeof frontmatter.value.title === 'string' ? frontmatter.value.title.trim() : ''
)

const link = computed(() => {
  const relativePath = page.value.relativePath
  if (!relativePath) return ''

  return `/${relativePath}`.replace(/\/index\.md$/, '/').replace(/\.md$/, '')
})

const iconName = computed(() => getSiteIconName(link.value, title.value))

const showHeading = computed(() => title.value !== '')
</script>

<template>
  <header v-if="showHeading" class="doc-page-heading">
    <div class="doc-page-heading__inner">
      <span v-if="iconName" class="doc-page-heading__icon-wrap" aria-hidden="true">
        <SiteIcon :name="iconName" class="doc-page-heading__icon" />
      </span>
      <h1 class="doc-page-heading__title">{{ title }}</h1>
    </div>
  </header>
</template>
