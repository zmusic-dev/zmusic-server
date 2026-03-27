<template>
  <ExternalLinkCard :links="links" :note="t.note" />
</template>

<script lang="ts" setup>
import { siCodeberg, siSpigotmc } from 'simple-icons'
import ExternalLinkCard from './ExternalLinkCard.vue'
import { useI18n } from '../utils/i18n'
import cnbSvg from '../assets/cnb.svg?raw'

const t = useI18n()

const links = [
  {
    url: 'https://codeberg.org/zmusic-dev/zmusic-server/releases',
    label: 'Codeberg',
    icon: 'codeberg',
    svg: siCodeberg.svg
  },
  {
    url: 'https://cnb.cool/zmusic-dev/zmusic-server/-/releases',
    label: 'CNB',
    icon: 'cnb',
    svg: cnbSvg
  },
  {
    url: 'https://www.spigotmc.org/resources/zmusic.83027/',
    label: 'SpigotMC',
    icon: 'spigotmc',
    svg: siSpigotmc.svg
  }
]
</script>
