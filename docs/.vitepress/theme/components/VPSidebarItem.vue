<script setup lang="ts">
import type { DefaultTheme } from 'vitepress/theme'
import { computed } from 'vue'
import { useSidebarItemControl } from 'vitepress/dist/client/theme-default/composables/sidebar.js'
import VPLink from 'vitepress/dist/client/theme-default/components/VPLink.vue'
import SiteIcon from './SiteIcon.vue'
import { getSiteIconName } from '../utils/site-icons'

const props = defineProps<{
  item: DefaultTheme.SidebarItem
  depth: number
}>()

const { collapsed, collapsible, isLink, isActiveLink, hasActiveLink, hasChildren, toggle } =
  useSidebarItemControl(computed(() => props.item))

const sectionTag = computed(() => (hasChildren.value ? 'section' : 'div'))
const linkTag = computed(() => (isLink.value ? 'a' : 'div'))

const textTag = computed(() => {
  return !hasChildren.value ? 'p' : props.depth + 2 === 7 ? 'p' : `h${props.depth + 2}`
})

const itemRole = computed(() => (isLink.value ? undefined : 'button'))

const classes = computed(() => [
  [`level-${props.depth}`],
  { collapsible: collapsible.value },
  { collapsed: collapsed.value },
  { 'is-link': isLink.value },
  { 'is-active': isActiveLink.value },
  { 'has-active': hasActiveLink.value }
])

const iconName = computed(() => {
  if (typeof props.item.text !== 'string') return null
  return getSiteIconName(
    typeof props.item.link === 'string' ? props.item.link : undefined,
    props.item.text
  )
})

function onItemInteraction(e: MouseEvent | Event) {
  if ('key' in e && e.key !== 'Enter') return
  !props.item.link && toggle()
}

function onCaretClick() {
  props.item.link && toggle()
}
</script>

<template>
  <component :is="sectionTag" class="VPSidebarItem" :class="classes">
    <div
      v-if="item.text"
      class="item"
      :role="itemRole"
      v-on="item.items ? { click: onItemInteraction, keydown: onItemInteraction } : {}"
      :tabindex="item.items && 0"
    >
      <div class="indicator" />

      <VPLink
        v-if="item.link"
        :tag="linkTag"
        class="link"
        :href="item.link"
        :rel="item.rel"
        :target="item.target"
      >
        <SiteIcon v-if="iconName" :name="iconName" class="sidebar-icon" />
        <component :is="textTag" class="text" v-html="item.text" />
      </VPLink>
      <template v-else>
        <SiteIcon v-if="iconName" :name="iconName" class="sidebar-icon" />
        <component :is="textTag" class="text" v-html="item.text" />
      </template>

      <div
        v-if="item.collapsed != null && item.items && item.items.length"
        class="caret"
        role="button"
        aria-label="toggle section"
        @click="onCaretClick"
        @keydown.enter="onCaretClick"
        tabindex="0"
      >
        <span class="vpi-chevron-right caret-icon" />
      </div>
    </div>

    <div v-if="item.items && item.items.length" class="items">
      <template v-if="depth < 5">
        <VPSidebarItem v-for="i in item.items" :key="i.text" :item="i" :depth="depth + 1" />
      </template>
    </div>
  </component>
</template>

<style scoped>
.VPSidebarItem.level-0 {
  padding-bottom: 24px;
}

.VPSidebarItem.collapsed.level-0 {
  padding-bottom: 10px;
}

.item {
  position: relative;
  display: flex;
  width: 100%;
}

.VPSidebarItem.collapsible > .item {
  cursor: pointer;
}

.indicator {
  position: absolute;
  top: 6px;
  bottom: 6px;
  left: -17px;
  width: 2px;
  border-radius: 2px;
  transition: background-color 0.25s;
}

.VPSidebarItem:is(.level-2, .level-3, .level-4, .level-5).is-active > .item > .indicator {
  background-color: var(--vp-c-brand-1);
}

.link {
  display: flex;
  align-items: center;
  flex-grow: 1;
}

.sidebar-icon {
  margin-right: 10px;
  font-size: 16px;
  color: var(--vp-c-text-1);
  flex-shrink: 0;
  transition: color 0.25s;
}

.text {
  flex-grow: 1;
  padding: 4px 0;
  line-height: 24px;
  font-size: 14px;
  transition: color 0.25s;
}

.VPSidebarItem.level-0 .text {
  font-weight: 700;
  color: var(--vp-c-text-1);
}

.VPSidebarItem:is(.level-1, .level-2, .level-3, .level-4, .level-5) .text {
  font-weight: 500;
  color: var(--vp-c-text-2);
}

.VPSidebarItem:is(.level-0, .level-1, .level-2, .level-3, .level-4, .level-5).is-link > .item > .link:hover .text,
.VPSidebarItem:is(.level-0, .level-1, .level-2, .level-3, .level-4, .level-5).is-link > .item > .link:hover .sidebar-icon {
  color: var(--vp-c-brand-1);
}

.VPSidebarItem:is(.level-0, .level-1, .level-2, .level-3, .level-4, .level-5).has-active > .item > .text,
.VPSidebarItem:is(.level-0, .level-1, .level-2, .level-3, .level-4, .level-5).has-active > .item > .sidebar-icon,
.VPSidebarItem:is(.level-0, .level-1, .level-2, .level-3, .level-4, .level-5).has-active > .item > .link > .text,
.VPSidebarItem:is(.level-0, .level-1, .level-2, .level-3, .level-4, .level-5).has-active > .item > .link > .sidebar-icon {
  color: var(--vp-c-text-1);
}

.VPSidebarItem:is(.level-0, .level-1, .level-2, .level-3, .level-4, .level-5).is-active > .item .link > .text,
.VPSidebarItem:is(.level-0, .level-1, .level-2, .level-3, .level-4, .level-5).is-active > .item .link > .sidebar-icon {
  color: var(--vp-c-brand-1);
}

.caret {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-right: -7px;
  width: 32px;
  height: 32px;
  color: var(--vp-c-text-3);
  cursor: pointer;
  transition: color 0.25s;
  flex-shrink: 0;
}

.item:hover .caret {
  color: var(--vp-c-text-2);
}

.item:hover .caret:hover {
  color: var(--vp-c-text-1);
}

.caret-icon {
  font-size: 18px;
  transform: rotate(90deg);
  transition: transform 0.25s;
}

.VPSidebarItem.collapsed .caret-icon {
  transform: rotate(0);
}

.VPSidebarItem:is(.level-1, .level-2, .level-3, .level-4, .level-5) .items {
  border-left: 1px solid var(--vp-c-divider);
  padding-left: 16px;
}

.VPSidebarItem.collapsed .items {
  display: none;
}
</style>
