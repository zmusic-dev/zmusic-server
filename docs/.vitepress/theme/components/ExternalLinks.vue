<template>
  <ExternalLinkCard :links="links" :note="t.note" />
</template>

<script lang="ts" setup>
import { computed } from 'vue'
import { useRoute } from 'vitepress'
import { siCodeberg, siGitee, siSpigotmc } from 'simple-icons'
import ExternalLinkCard from './ExternalLinkCard.vue'
import { getSiteLocale, useI18n } from '../utils/i18n'

const route = useRoute()
const t = useI18n()
const locale = computed(() => getSiteLocale(route.path))

const links = computed(() => {
  const baseLinks = [
    {
      url: 'https://codeberg.org/zmusic-dev/zmusic-server/releases',
      label: 'Codeberg',
      icon: 'codeberg',
      svg: siCodeberg.svg
    },
    {
      url: 'https://www.spigotmc.org/resources/zmusic.83027/',
      label: 'SpigotMC',
      icon: 'spigotmc',
      svg: siSpigotmc.svg
    }
  ]

  // Gitee only visible for Chinese users
  if (locale.value === '/') {
    baseLinks.splice(1, 0, {
      url: 'https://gitee.com/zmusic-dev/zmusic-server/releases',
      label: 'Gitee',
      icon: 'gitee',
      svg: siGitee.svg
    })
  }

  return baseLinks
})
</script>
