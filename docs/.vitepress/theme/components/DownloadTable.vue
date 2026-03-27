<template>
    <div class="download-section">
        <div v-if="loading" class="download-loading">{{ t.loading }}</div>
        <div v-else-if="error" class="download-empty">{{ t.error }}</div>
        <template v-else>
            <div class="download-tabs">
                <button
                    v-for="tab in tabs"
                    :key="tab.key"
                    class="tab-button"
                    :class="{ active: activeTab === tab.key }"
                    @click="activeTab = tab.key"
                >
                    {{ tab.label }}
                </button>
            </div>

            <div class="download-content">
                <!-- 稳定版 -->
                <div v-if="activeTab === 'stable'" class="tab-panel">
                    <div class="panel-header">
                        <span class="version-tag">{{ stableVersion }}</span>
                        <a
                            :href="`https://github.com/${getRepo()}/releases/latest`"
                            target="_blank"
                            rel="noreferrer"
                            class="btn-secondary"
                        >
                            {{ t.viewRelease }}
                        </a>
                    </div>
                    <div class="download-list">
                        <div
                            v-for="file in stableFiles"
                            :key="file.link"
                            class="download-row"
                        >
                            <span class="download-filename">{{
                                file.name
                            }}</span>
                            <a
                                :href="file.link"
                                target="_blank"
                                rel="noreferrer"
                                class="btn-download"
                                >{{ t.download }}</a
                            >
                        </div>
                    </div>
                </div>

                <!-- 开发版 -->
                <div v-if="activeTab === 'dev'" class="tab-panel">
                    <div class="panel-header">
                        <span class="version-tag">{{ devVersion }}</span>
                        <a
                            :href="devReleaseUrl"
                            target="_blank"
                            rel="noreferrer"
                            class="btn-secondary"
                        >
                            {{ t.viewBuild }}
                        </a>
                    </div>
                    <p class="panel-desc">{{ t.devWarning }}</p>
                    <div class="download-list">
                        <div
                            v-for="file in devFiles"
                            :key="file.link"
                            class="download-row"
                        >
                            <span class="download-filename">{{
                                file.name
                            }}</span>
                            <a
                                :href="file.link"
                                target="_blank"
                                rel="noreferrer"
                                class="btn-download"
                                >{{ t.download }}</a
                            >
                        </div>
                    </div>
                </div>

                <!-- Addon -->
                <div v-if="activeTab === 'addon'" class="tab-panel">
                    <div class="panel-header">
                        <span class="version-tag">{{ addonVersion }}</span>
                        <a
                            :href="addonReleaseUrl"
                            target="_blank"
                            rel="noreferrer"
                            class="btn-secondary"
                        >
                            {{ t.viewRelease }}
                        </a>
                    </div>
                    <div class="download-list">
                        <div
                            v-for="file in addonFiles"
                            :key="file.link"
                            class="download-row"
                        >
                            <span class="download-filename">{{
                                file.name
                            }}</span>
                            <a
                                :href="file.link"
                                target="_blank"
                                rel="noreferrer"
                                class="btn-download"
                                >{{ t.download }}</a
                            >
                        </div>
                    </div>
                </div>
            </div>
        </template>
    </div>
</template>

<script lang="ts" setup>
import { computed, onMounted, ref } from "vue";
import { useData } from "vitepress";
import { useI18n } from "../utils/i18n";

interface DownloadFile {
    name: string;
    link: string;
}

const t = useI18n();

const tabs = computed(() => [
    { key: "stable", label: t.value.tabs.stable },
    { key: "dev", label: t.value.tabs.dev },
    { key: "addon", label: t.value.tabs.addon },
]);

const activeTab = ref("stable");

const loading = ref(true);
const error = ref(false);

// 稳定版数据
const stableVersion = ref("");
const stableFiles = ref<DownloadFile[]>([]);

// 开发版数据
const devVersion = ref("");
const devFiles = ref<DownloadFile[]>([]);
const devReleaseUrl = ref("");

// Addon 数据
const addonVersion = ref("");
const addonFiles = ref<DownloadFile[]>([]);
const addonReleaseUrl = ref("");

const { theme } = useData();
const getRepo = () => theme.value.repo as string;

onMounted(async () => {
    try {
        const repo = getRepo();

        const [releasesData, actionsData] = await Promise.all([
            fetch(`https://api.github.com/repos/${repo}/releases?per_page=50`),
            fetch(
                `https://api.github.com/repos/${repo}/actions/workflows/dev.yml/runs?per_page=1&status=success`,
            ),
        ]);

        const releases = await releasesData.json();
        const actions = await actionsData.json();

        // 稳定版：最新的 release，过滤掉预发布版本
        const stableRelease = releases.find(
            (r: { prerelease: boolean }) => !r.prerelease,
        );
        if (stableRelease) {
            const tag = stableRelease.tag_name;
            stableVersion.value = tag.startsWith("v") ? tag : `v${tag}`;
            stableFiles.value = stableRelease.assets
                .filter(
                    (a: { name: string }) =>
                        a.name.endsWith(".jar") && !a.name.includes("Addon"),
                )
                .map((a: { name: string; browser_download_url: string }) => ({
                    name: a.name,
                    link: a.browser_download_url,
                }));
        }

        // Addon：找到包含 Addon.jar 的最新 release
        const addonRelease = releases.find(
            (r: { assets: { name: string }[] }) =>
                r.assets.some((a: { name: string }) =>
                    a.name.includes("Addon"),
                ),
        );
        if (addonRelease) {
            addonReleaseUrl.value = addonRelease.html_url;
            addonFiles.value = addonRelease.assets
                .filter(
                    (a: { name: string }) =>
                        a.name.includes("Addon") && a.name.endsWith(".jar"),
                )
                .map((a: { name: string; browser_download_url: string }) => ({
                    name: a.name,
                    link: a.browser_download_url,
                }));
            // 从文件名解析 Addon 版本号
            if (addonFiles.value.length > 0) {
                const match = addonFiles.value[0].name.match(
                    /addon-(\d+\.\d+\.\d+)/i,
                );
                addonVersion.value = match
                    ? `v${match[1]}`
                    : addonRelease.tag_name;
            }
        }

        // 开发版
        if (actions.workflow_runs?.[0]) {
            const run = actions.workflow_runs[0];
            devVersion.value = `v4.0.0-dev.${run.head_sha.substring(0, 7)}`;
            devReleaseUrl.value = run.html_url;

            const artifactsResponse = await fetch(
                `https://api.github.com/repos/${repo}/actions/runs/${run.id}/artifacts`,
            );
            const artifactsData = await artifactsResponse.json();

            devFiles.value = artifactsData.artifacts.map(
                (a: { name: string; id: number }) => ({
                    name: `${a.name}.zip`,
                    link: `https://github.com/${repo}/actions/runs/${run.id}/artifacts/${a.id}`,
                }),
            );
        }

        loading.value = false;
    } catch (e) {
        console.error(e);
        error.value = true;
        loading.value = false;
    }
});
</script>

<style scoped>
.download-section {
    margin: 1rem 0;
}

.download-loading {
    text-align: center;
    padding: 2rem;
    color: var(--vp-c-text-2);
}

.download-empty {
    text-align: center;
    padding: 2rem;
    color: var(--vp-c-danger-1);
}

.download-tabs {
    display: flex;
    gap: 0.5rem;
    margin-bottom: 1rem;
    border-bottom: 1px solid var(--vp-c-divider);
    padding-bottom: 0.5rem;
}

.tab-button {
    padding: 0.5rem 1rem;
    font-size: 0.9rem;
    font-weight: 500;
    color: var(--vp-c-text-2);
    background: transparent;
    border: none;
    border-radius: 6px;
    cursor: pointer;
    transition: all 0.2s;
}

.tab-button:hover {
    color: var(--vp-c-text-1);
    background: var(--vp-c-default-soft);
}

.tab-button.active {
    color: var(--vp-c-brand-1);
    background: var(--vp-c-brand-soft);
}

.download-content {
    border: 1px solid var(--vp-c-divider);
    border-radius: 12px;
    padding: 1.25rem;
    background: transparent;
    backdrop-filter: blur(16px);
    -webkit-backdrop-filter: blur(16px);
}

.panel-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 1rem;
    gap: 0.75rem;
}

.version-tag {
    font-size: 0.85rem;
    font-weight: 500;
    color: var(--vp-c-brand-1);
    background: var(--vp-c-brand-soft);
    padding: 0.25rem 0.75rem;
    border-radius: 6px;
}

.panel-desc {
    font-size: 0.85rem;
    color: var(--vp-c-warning-1);
    margin-bottom: 1rem;
    padding: 0.5rem 0.75rem;
    background: var(--vp-c-warning-soft);
    border-radius: 6px;
}

.download-list {
    display: flex;
    flex-direction: column;
    gap: 0.5rem;
}

.download-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    gap: 1rem;
    padding: 0.75rem 1rem;
    border: 1px solid var(--vp-c-divider);
    border-radius: 8px;
    transition: border-color 0.2s;
}

.download-row:hover {
    border-color: var(--vp-c-brand-1);
}

.download-filename {
    font-family: var(--vp-font-family-mono);
    font-size: 0.875rem;
    color: var(--vp-c-text-1);
    word-break: break-all;
}

.btn-download {
    font-size: 0.875rem;
    font-weight: 500;
    color: var(--vp-c-white);
    background: var(--vp-c-brand-1);
    text-decoration: none;
    padding: 0.5rem 1rem;
    border-radius: 8px;
    transition: background 0.2s;
    white-space: nowrap;
}

.btn-download:hover {
    background: var(--vp-c-brand-2);
}

.btn-secondary {
    font-size: 0.85rem;
    color: var(--vp-c-text-2);
    text-decoration: none;
    padding: 0.4rem 0.8rem;
    border-radius: 6px;
    border: 1px solid var(--vp-c-divider);
    transition: all 0.2s;
}

.btn-secondary:hover {
    color: var(--vp-c-text-1);
    border-color: var(--vp-c-text-2);
}

@media (max-width: 639px) {
    .download-tabs {
        flex-wrap: wrap;
    }

    .panel-header {
        flex-direction: column;
        align-items: flex-start;
    }

    .download-row {
        flex-direction: column;
        align-items: stretch;
        text-align: center;
    }

    .btn-download {
        justify-content: center;
        text-align: center;
    }
}
</style>
