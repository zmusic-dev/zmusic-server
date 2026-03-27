<template>
  <component :is="icon" class="site-icon" aria-hidden="true" />
</template>

<script lang="ts" setup>
import { computed } from 'vue'
import {
  Home,
  Lightbulb,
  CircleHelp,
  Info,
  Music,
  MessageCircle,
  MessagesSquare,
  BookOpen,
  Signpost,
  Terminal,
  Shield,
  Settings
} from '@lucide/vue'

const props = defineProps<{
  name: string
}>()

const iconMap: Record<string, typeof Home> = {
  home: Home,
  lightbulb: Lightbulb,
  'circle-help': CircleHelp,
  'circle-info': Info,
  music: Music,
  'message-circle': MessageCircle,
  'messages-square': MessagesSquare,
  'book-open': BookOpen,
  signpost: Signpost,
  terminal: Terminal,
  shield: Shield,
  settings: Settings
}

const icon = computed(() => iconMap[props.name] || Home)
</script>

<style scoped>
.site-icon {
  display: block;
  width: 1em;
  height: 1em;
}
</style>
