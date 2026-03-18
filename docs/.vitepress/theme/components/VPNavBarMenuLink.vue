<script lang="ts" setup>
import type { DefaultTheme } from 'vitepress/theme'
import { computed } from 'vue'
import { useData } from 'vitepress/dist/client/theme-default/composables/data.js'
import { isActive } from 'vitepress/dist/client/shared.js'
import VPLink from 'vitepress/dist/client/theme-default/components/VPLink.vue'
import SiteIcon from './SiteIcon.vue'
import { getSiteIconName } from '../utils/site-icons'

const props = defineProps<{
  item: DefaultTheme.NavItemWithLink
}>()

const { page } = useData()

const href = computed(() =>
  typeof props.item.link === 'function' ? props.item.link(page.value) : props.item.link
)

const iconName = computed(() => getSiteIconName(href.value, props.item.text))
</script>

<template>
  <VPLink
    :class="{
      VPNavBarMenuLink: true,
      active: isActive(page.relativePath, item.activeMatch || href, !!item.activeMatch)
    }"
    :href
    :target="item.target"
    :rel="item.rel"
    :no-icon="item.noIcon"
    tabindex="0"
  >
    <span class="content">
      <SiteIcon v-if="iconName" :name="iconName" class="nav-icon" />
      <span v-html="item.text"></span>
    </span>
  </VPLink>
</template>

<style scoped>
.VPNavBarMenuLink {
  display: flex;
  align-items: center;
  padding: 0 12px;
  line-height: var(--vp-nav-height);
  font-size: 14px;
  font-weight: 500;
  color: var(--vp-c-text-1);
  transition: color 0.25s;
}

.content {
  display: inline-flex;
  align-items: center;
  gap: 7px;
}

.nav-icon {
  font-size: 15px;
  color: currentColor;
  opacity: 0.92;
}

.VPNavBarMenuLink.active {
  color: var(--vp-c-brand-1);
}

.VPNavBarMenuLink:hover {
  color: var(--vp-c-brand-1);
}
</style>
