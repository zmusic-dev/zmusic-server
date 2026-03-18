<script lang="ts" setup generic="T extends DefaultTheme.NavItemWithLink">
import type { DefaultTheme } from 'vitepress/theme'
import { computed } from 'vue'
import { useData } from 'vitepress/dist/client/theme-default/composables/data.js'
import { isActive } from 'vitepress/dist/client/shared.js'
import VPLink from 'vitepress/dist/client/theme-default/components/VPLink.vue'
import SiteIcon from './SiteIcon.vue'
import { getSiteIconName } from '../utils/site-icons'

const props = defineProps<{
  item: T
}>()

const { page } = useData()

const href = computed(() =>
  typeof props.item.link === 'function' ? props.item.link(page.value) : props.item.link
)

const iconName = computed(() => getSiteIconName(href.value, props.item.text))

defineOptions({ inheritAttrs: false })
</script>

<template>
  <div class="VPMenuLink">
    <VPLink
      v-bind="$attrs"
      :class="{
        active: isActive(page.relativePath, item.activeMatch || href, !!item.activeMatch)
      }"
      :href
      :target="item.target"
      :rel="item.rel"
      :no-icon="item.noIcon"
    >
      <span class="content">
        <SiteIcon v-if="iconName" :name="iconName" class="menu-icon" />
        <span v-html="item.text"></span>
      </span>
    </VPLink>
  </div>
</template>

<style scoped>
.VPMenuGroup + .VPMenuLink {
  margin: 12px -12px 0;
  border-top: 1px solid var(--vp-c-divider);
  padding: 12px 12px 0;
}

.link {
  display: block;
  border-radius: 6px;
  padding: 0 12px;
  line-height: 32px;
  font-size: 14px;
  font-weight: 500;
  color: var(--vp-c-text-1);
  text-align: left;
  white-space: nowrap;
  transition:
    background-color 0.25s,
    color 0.25s;
}

.content {
  display: inline-flex;
  align-items: center;
  gap: 8px;
}

.menu-icon {
  font-size: 15px;
  color: currentColor;
  opacity: 0.92;
}

.link:hover {
  color: var(--vp-c-brand-1);
  background-color: var(--vp-c-default-soft);
}

.link.active {
  color: var(--vp-c-brand-1);
}
</style>
