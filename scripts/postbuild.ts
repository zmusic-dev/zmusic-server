import { mkdir, rm, stat, writeFile } from 'node:fs/promises'
import path from 'node:path'

type Redirect = {
  source: string
  destination: string
  lang: string
}

const distDir = path.resolve('docs/.vitepress/dist')

const redirects: Redirect[] = [
  {
    source: '/netease-api.html',
    destination: '/netease-api/public.html',
    lang: 'zh-CN'
  },
  {
    source: '/netease-api/index.html',
    destination: '/netease-api/public.html',
    lang: 'zh-CN'
  },
  {
    source: '/zh-cn/deploy-api/netease-api.html',
    destination: '/netease-api/public.html',
    lang: 'zh-CN'
  },
  {
    source: '/en/netease-api.html',
    destination: '/en/netease-api/public.html',
    lang: 'en-US'
  },
  {
    source: '/en/netease-api/index.html',
    destination: '/en/netease-api/public.html',
    lang: 'en-US'
  },
  {
    source: '/ja/netease-api.html',
    destination: '/ja/netease-api/public.html',
    lang: 'ja-JP'
  },
  {
    source: '/ja/netease-api/index.html',
    destination: '/ja/netease-api/public.html',
    lang: 'ja-JP'
  }
]

function renderRedirectHtml({ destination, lang }: Redirect): string {
  return `<!doctype html>
<html lang="${lang}">
  <head>
    <meta charset="utf-8" />
    <meta http-equiv="refresh" content="0; url=${destination}" />
    <meta name="robots" content="noindex" />
    <link rel="canonical" href="${destination}" />
    <script>
      location.replace(${JSON.stringify(destination)} + location.search + location.hash)
    </script>
    <title>Redirecting...</title>
  </head>
  <body>
    <p>Redirecting to <a href="${destination}">${destination}</a>...</p>
  </body>
</html>
`
}

function getErrorCode(error: unknown): string | undefined {
  if (typeof error === 'object' && error !== null && 'code' in error) {
    const { code } = error as { code?: unknown }
    return typeof code === 'string' ? code : undefined
  }

  return undefined
}

async function removeDirectoryAtFilePath(filePath: string): Promise<void> {
  try {
    const fileStat = await stat(filePath)

    if (fileStat.isDirectory()) {
      await rm(filePath, { recursive: true, force: true })
    }
  } catch (error) {
    if (getErrorCode(error) !== 'ENOENT') {
      throw error
    }
  }
}

async function ensureParentDir(filePath: string): Promise<void> {
  await mkdir(path.dirname(filePath), { recursive: true })
}

async function generateRedirect(redirect: Redirect): Promise<void> {
  const outputFile = path.join(distDir, redirect.source.replace(/^\/+/, ''))

  await removeDirectoryAtFilePath(outputFile)
  await ensureParentDir(outputFile)
  await writeFile(outputFile, renderRedirectHtml(redirect), 'utf8')
}

async function ensureDistExists(): Promise<void> {
  try {
    const distStat = await stat(distDir)

    if (!distStat.isDirectory()) {
      throw new Error(`构建输出目录不存在: ${distDir}`)
    }
  } catch (error) {
    if (getErrorCode(error) === 'ENOENT') {
      throw new Error(`构建输出目录不存在: ${distDir}`)
    }

    throw error
  }
}

async function main(): Promise<void> {
  await ensureDistExists()
  await Promise.all(redirects.map(generateRedirect))
  console.log(`[postbuild] generated ${redirects.length} redirect files`)
}

await main()
