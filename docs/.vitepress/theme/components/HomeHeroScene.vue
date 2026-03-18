<template>
  <div class="hero-scene" aria-hidden="true">
    <div class="hero-scene__glow hero-scene__glow--primary" />
    <div class="hero-scene__glow hero-scene__glow--secondary" />
    <div class="hero-scene__glow hero-scene__glow--accent" />
    <div class="hero-scene__orbit hero-scene__orbit--outer" />
    <div class="hero-scene__orbit hero-scene__orbit--inner" />

    <div class="hero-scene__pill hero-scene__pill--status">
      <span class="hero-scene__pill-dot" />
      <span>{{ t.status }}</span>
    </div>

    <div class="hero-scene__card hero-scene__card--signal">
      <div class="hero-scene__bars" aria-hidden="true">
        <span />
        <span />
        <span />
        <span />
      </div>
      <span>{{ t.signal }}</span>
    </div>

    <div class="hero-scene__card hero-scene__card--platforms">
      <span class="hero-scene__card-label">{{ t.platform }}</span>
      <div class="hero-scene__chips">
        <span>Spigot</span>
        <span>BungeeCord</span>
        <span>Velocity</span>
      </div>
    </div>

    <div class="hero-scene__spark hero-scene__spark--one" />
    <div class="hero-scene__spark hero-scene__spark--two" />

    <div class="hero-scene__frame">
      <div class="hero-scene__frame-shine" />
      <img class="hero-scene__image hero-scene__image--dark" :src="heroImageDarkSrc" alt="" />
      <img class="hero-scene__image hero-scene__image--light" :src="heroImageLightSrc" alt="" />
    </div>
  </div>
</template>

<script lang="ts" setup>
import { computed } from 'vue'
import { useRoute, withBase } from 'vitepress'
import { getSiteLocale } from '../utils/locale'

const route = useRoute()
const heroImageDarkSrc = withBase('/images/hero-zmusic.svg')
const heroImageLightSrc = withBase('/images/hero-zmusic-light.svg')

const copy = {
  '/': {
    status: '正在播放',
    signal: '平滑输出',
    platform: '跨平台'
  },
  '/en/': {
    status: 'Now Playing',
    signal: 'Smooth Output',
    platform: 'Platforms'
  },
  '/zh-tw/': {
    status: '正在播放',
    signal: '平滑輸出',
    platform: '跨平台'
  },
  '/ja/': {
    status: '再生中',
    signal: 'スムーズ出力',
    platform: '対応環境'
  }
} as const

const locale = computed(() => getSiteLocale(route.path))
const t = computed(() => copy[locale.value])
</script>

<style scoped>
.hero-scene {
  position: relative;
  width: min(100%, 560px);
  aspect-ratio: 1;
  margin: 0 auto;
  pointer-events: none;
  isolation: isolate;
}

.hero-scene__glow,
.hero-scene__orbit,
.hero-scene__spark,
.hero-scene__frame,
.hero-scene__pill,
.hero-scene__card {
  position: absolute;
}

.hero-scene__glow {
  border-radius: 999px;
  filter: blur(28px);
  opacity: 0.8;
}

.hero-scene__glow--primary {
  inset: 20% auto auto 12%;
  width: 34%;
  height: 34%;
  background:
    radial-gradient(circle at 30% 30%, rgba(124, 199, 255, 0.95), rgba(0, 136, 255, 0.1) 68%, transparent 78%);
  animation: hero-drift-primary 9s ease-in-out infinite;
}

.hero-scene__glow--secondary {
  inset: auto 10% 14% auto;
  width: 30%;
  height: 30%;
  background:
    radial-gradient(circle at 50% 50%, rgba(0, 136, 255, 0.75), rgba(0, 136, 255, 0.08) 64%, transparent 78%);
  animation: hero-drift-secondary 10s ease-in-out infinite;
}

.hero-scene__glow--accent {
  inset: 42% auto auto 56%;
  width: 22%;
  height: 22%;
  background:
    radial-gradient(circle at 50% 50%, rgba(255, 255, 255, 0.78), rgba(124, 199, 255, 0.08) 60%, transparent 78%);
  filter: blur(34px);
  animation: hero-pulse 6.5s ease-in-out infinite;
}

.hero-scene__orbit {
  border-radius: 999px;
  border: 1px solid rgba(124, 199, 255, 0.24);
  background: rgba(255, 255, 255, 0.03);
}

.hero-scene__orbit--outer {
  inset: 10% 6% 8%;
  box-shadow: inset 0 0 0 1px rgba(255, 255, 255, 0.04);
  animation: hero-spin 22s linear infinite;
}

.hero-scene__orbit--inner {
  inset: 22% 18%;
  opacity: 0.56;
  animation: hero-spin-reverse 18s linear infinite;
}

.hero-scene__frame {
  inset: 14% 12% 12%;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 34px;
  background:
    linear-gradient(155deg, rgba(9, 24, 54, 0.54), rgba(7, 21, 43, 0.18)),
    radial-gradient(circle at 18% 12%, rgba(124, 199, 255, 0.2), transparent 32%);
  border: 1px solid rgba(124, 199, 255, 0.2);
  box-shadow:
    0 28px 88px rgba(0, 136, 255, 0.2),
    0 20px 50px rgba(15, 23, 42, 0.22),
    inset 0 1px 0 rgba(255, 255, 255, 0.16);
  backdrop-filter: blur(18px) saturate(150%);
  transform: perspective(1400px) rotateX(9deg) rotateY(-11deg) rotateZ(-2deg);
  transform-style: preserve-3d;
  animation: hero-float 7.2s ease-in-out infinite;
  overflow: hidden;
}

.hero-scene__frame::before {
  position: absolute;
  inset: 0;
  content: '';
  background:
    linear-gradient(120deg, transparent 0%, rgba(255, 255, 255, 0.12) 35%, transparent 62%),
    radial-gradient(circle at 78% 20%, rgba(255, 255, 255, 0.14), transparent 30%);
  opacity: 0.8;
}

.hero-scene__frame::after {
  position: absolute;
  inset: 18px;
  border-radius: 24px;
  content: '';
  border: 1px solid rgba(255, 255, 255, 0.08);
}

.hero-scene__frame-shine {
  position: absolute;
  inset: -30% auto auto -18%;
  width: 48%;
  height: 180%;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.26), transparent 70%);
  transform: rotate(18deg);
  opacity: 0.4;
  filter: blur(8px);
}

.hero-scene__image {
  position: relative;
  z-index: 1;
  width: 92%;
  height: 92%;
  object-fit: contain;
  filter: drop-shadow(0 18px 42px rgba(0, 136, 255, 0.24));
  transform: translateZ(18px) scale(1.04);
}

.hero-scene__image--light {
  display: none;
}

:global(html:not(.dark) .hero-scene__image--dark) {
  display: none;
}

:global(html:not(.dark) .hero-scene__image--light) {
  display: block;
}

.hero-scene__pill,
.hero-scene__card {
  z-index: 2;
  display: flex;
  align-items: center;
  gap: 10px;
  border: 1px solid rgba(124, 199, 255, 0.18);
  background: rgba(9, 24, 54, 0.16);
  box-shadow:
    0 16px 34px rgba(15, 23, 42, 0.18),
    inset 0 1px 0 rgba(255, 255, 255, 0.12);
  backdrop-filter: blur(18px) saturate(160%);
  color: rgba(255, 255, 255, 0.92);
}

.hero-scene__pill {
  top: 12%;
  left: 0;
  padding: 10px 14px;
  border-radius: 999px;
  font-size: 13px;
  font-weight: 700;
  letter-spacing: 0.02em;
  animation: hero-chip-float-left 6.8s ease-in-out infinite;
}

.hero-scene__pill-dot {
  width: 8px;
  height: 8px;
  border-radius: 999px;
  background: #63f2c8;
  box-shadow: 0 0 0 6px rgba(99, 242, 200, 0.12);
  animation: hero-pulse-dot 2.2s ease-in-out infinite;
}

.hero-scene__card {
  border-radius: 22px;
}

.hero-scene__card--signal {
  top: 10%;
  right: 2%;
  padding: 14px 16px;
  min-width: 152px;
  justify-content: space-between;
  animation: hero-chip-float-right 7.4s ease-in-out infinite;
}

.hero-scene__bars {
  display: flex;
  align-items: flex-end;
  gap: 6px;
  height: 24px;
}

.hero-scene__bars span {
  width: 6px;
  border-radius: 999px;
  background: linear-gradient(180deg, #7cc7ff, #0088ff);
  animation: hero-bar-bounce 1.2s ease-in-out infinite;
}

.hero-scene__bars span:nth-child(1) {
  height: 10px;
  animation-delay: 0s;
}

.hero-scene__bars span:nth-child(2) {
  height: 18px;
  animation-delay: 0.15s;
}

.hero-scene__bars span:nth-child(3) {
  height: 24px;
  animation-delay: 0.3s;
}

.hero-scene__bars span:nth-child(4) {
  height: 14px;
  animation-delay: 0.45s;
}

.hero-scene__card--platforms {
  left: 3%;
  right: 16%;
  bottom: 3%;
  flex-direction: column;
  align-items: flex-start;
  gap: 12px;
  padding: 16px 18px;
  animation: hero-chip-float-left 7.6s ease-in-out infinite;
}

.hero-scene__card-label {
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  color: rgba(226, 232, 240, 0.72);
}

.hero-scene__chips {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.hero-scene__chips span {
  padding: 6px 10px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.1);
  font-size: 11px;
  font-weight: 700;
  line-height: 1;
  white-space: nowrap;
}

:global(html:not(.dark) .hero-scene__glow--primary) {
  background:
    radial-gradient(circle at 30% 30%, rgba(124, 199, 255, 0.78), rgba(0, 136, 255, 0.08) 68%, transparent 78%);
}

:global(html:not(.dark) .hero-scene__glow--secondary) {
  background:
    radial-gradient(circle at 50% 50%, rgba(0, 136, 255, 0.46), rgba(0, 136, 255, 0.04) 64%, transparent 78%);
}

:global(html:not(.dark) .hero-scene__glow--accent) {
  background:
    radial-gradient(circle at 50% 50%, rgba(255, 255, 255, 0.96), rgba(124, 199, 255, 0.04) 60%, transparent 78%);
}

:global(html:not(.dark) .hero-scene__orbit) {
  border-color: rgba(0, 136, 255, 0.18);
  background: rgba(255, 255, 255, 0.14);
}

:global(html:not(.dark) .hero-scene__orbit--outer) {
  box-shadow: inset 0 0 0 1px rgba(0, 136, 255, 0.08);
}

:global(html:not(.dark) .hero-scene__frame) {
  background:
    linear-gradient(155deg, rgba(255, 255, 255, 0.56), rgba(226, 239, 255, 0.2)),
    radial-gradient(circle at 18% 12%, rgba(124, 199, 255, 0.32), transparent 34%);
  border-color: rgba(0, 136, 255, 0.18);
  box-shadow:
    0 24px 72px rgba(0, 136, 255, 0.12),
    0 18px 44px rgba(71, 85, 105, 0.14),
    inset 0 1px 0 rgba(255, 255, 255, 0.72);
}

:global(html:not(.dark) .hero-scene__frame::before) {
  background:
    linear-gradient(120deg, transparent 0%, rgba(255, 255, 255, 0.62) 35%, transparent 62%),
    radial-gradient(circle at 78% 20%, rgba(255, 255, 255, 0.52), transparent 30%);
}

:global(html:not(.dark) .hero-scene__frame::after) {
  border-color: rgba(0, 136, 255, 0.08);
}

:global(html:not(.dark) .hero-scene__frame-shine) {
  opacity: 0.56;
}

:global(html:not(.dark) .hero-scene__image) {
  filter: drop-shadow(0 18px 36px rgba(0, 136, 255, 0.16));
}

:global(html:not(.dark) .hero-scene__pill),
:global(html:not(.dark) .hero-scene__card) {
  border-color: rgba(0, 136, 255, 0.16);
  background: rgba(255, 255, 255, 0.28);
  box-shadow:
    0 16px 34px rgba(71, 85, 105, 0.12),
    inset 0 1px 0 rgba(255, 255, 255, 0.72);
  color: rgba(15, 23, 42, 0.88);
}

:global(html:not(.dark) .hero-scene__card-label) {
  color: rgba(30, 58, 90, 0.7);
}

:global(html:not(.dark) .hero-scene__chips span) {
  background: rgba(255, 255, 255, 0.42);
  border-color: rgba(0, 136, 255, 0.1);
  color: rgba(30, 58, 90, 0.88);
}

.hero-scene__spark {
  z-index: 1;
  width: 14px;
  height: 14px;
  border-radius: 4px;
  background: linear-gradient(135deg, rgba(124, 199, 255, 0.95), rgba(0, 136, 255, 0.4));
  box-shadow: 0 0 24px rgba(0, 136, 255, 0.28);
}

.hero-scene__spark--one {
  top: 24%;
  right: 16%;
  transform: rotate(22deg);
  animation: hero-spark-drift 5.8s ease-in-out infinite;
}

.hero-scene__spark--two {
  bottom: 16%;
  right: 8%;
  width: 18px;
  height: 18px;
  border-radius: 6px;
  transform: rotate(-18deg);
  animation: hero-spark-drift 6.4s ease-in-out infinite reverse;
}

@keyframes hero-float {
  0%,
  100% {
    transform: perspective(1400px) rotateX(9deg) rotateY(-11deg) rotateZ(-2deg) translateY(0);
  }

  50% {
    transform: perspective(1400px) rotateX(7deg) rotateY(-8deg) rotateZ(0deg) translateY(-14px);
  }
}

@keyframes hero-drift-primary {
  0%,
  100% {
    transform: translate3d(0, 0, 0);
  }

  50% {
    transform: translate3d(18px, -16px, 0);
  }
}

@keyframes hero-drift-secondary {
  0%,
  100% {
    transform: translate3d(0, 0, 0);
  }

  50% {
    transform: translate3d(-12px, -10px, 0);
  }
}

@keyframes hero-pulse {
  0%,
  100% {
    opacity: 0.72;
    transform: scale(0.96);
  }

  50% {
    opacity: 1;
    transform: scale(1.08);
  }
}

@keyframes hero-chip-float-left {
  0%,
  100% {
    transform: translate3d(0, 0, 0);
  }

  50% {
    transform: translate3d(-10px, -12px, 0);
  }
}

@keyframes hero-chip-float-right {
  0%,
  100% {
    transform: translate3d(0, 0, 0);
  }

  50% {
    transform: translate3d(12px, -10px, 0);
  }
}

@keyframes hero-bar-bounce {
  0%,
  100% {
    transform: scaleY(0.7);
    opacity: 0.72;
  }

  50% {
    transform: scaleY(1.15);
    opacity: 1;
  }
}

@keyframes hero-pulse-dot {
  0%,
  100% {
    box-shadow: 0 0 0 6px rgba(99, 242, 200, 0.12);
    opacity: 0.82;
  }

  50% {
    box-shadow: 0 0 0 10px rgba(99, 242, 200, 0.08);
    opacity: 1;
  }
}

@keyframes hero-spark-drift {
  0%,
  100% {
    transform: translate3d(0, 0, 0) rotate(22deg);
    opacity: 0.72;
  }

  50% {
    transform: translate3d(4px, -16px, 0) rotate(38deg);
    opacity: 1;
  }
}

@keyframes hero-spin {
  from {
    transform: rotate(0deg);
  }

  to {
    transform: rotate(360deg);
  }
}

@keyframes hero-spin-reverse {
  from {
    transform: rotate(360deg);
  }

  to {
    transform: rotate(0deg);
  }
}

@media (max-width: 959px) {
  .hero-scene {
    width: min(100%, 500px);
  }

  .hero-scene__frame {
    inset: 14% 12% 14%;
  }

  .hero-scene__card--platforms {
    right: 10%;
  }
}

@media (max-width: 639px) {
  .hero-scene {
    width: min(100%, 360px);
  }

  .hero-scene__pill {
    top: 10%;
    left: 4%;
    padding: 8px 12px;
    font-size: 12px;
  }

  .hero-scene__card--signal {
    top: 10%;
    right: 4%;
    min-width: 124px;
    padding: 12px 14px;
    font-size: 12px;
  }

  .hero-scene__bars {
    gap: 4px;
    height: 20px;
  }

  .hero-scene__bars span {
    width: 5px;
  }

  .hero-scene__card--platforms {
    left: 5%;
    right: 5%;
    bottom: 4%;
    padding: 14px 14px 12px;
    gap: 10px;
  }

  .hero-scene__chips {
    gap: 6px;
  }

  .hero-scene__chips span {
    padding: 5px 8px;
    font-size: 10px;
  }

  .hero-scene__spark--one {
    top: 28%;
    right: 18%;
  }

  .hero-scene__spark--two {
    bottom: 20%;
    right: 8%;
  }
}

@media (prefers-reduced-motion: reduce) {
  .hero-scene__glow,
  .hero-scene__orbit,
  .hero-scene__spark,
  .hero-scene__frame,
  .hero-scene__pill,
  .hero-scene__card,
  .hero-scene__bars span,
  .hero-scene__pill-dot {
    animation: none !important;
  }
}
</style>
