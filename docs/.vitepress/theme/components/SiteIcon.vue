<template>
  <svg
    class="site-icon"
    viewBox="0 0 24 24"
    fill="none"
    stroke="currentColor"
    stroke-width="1.9"
    stroke-linecap="round"
    stroke-linejoin="round"
    aria-hidden="true"
    v-html="icon"
  />
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { siteIcons, type SiteIconName } from '../utils/site-icons'

const props = defineProps<{
  name: SiteIconName
}>()

const icon = computed(() => siteIcons[props.name])
</script>

<style scoped>
.site-icon {
  display: block;
  width: 1em;
  height: 1em;
}
</style>
