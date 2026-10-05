const CACHE_NAME = 'storylingo-v2';
const ASSETS_TO_CACHE = [
  './',
  './index.html',
  './icon.png',
  './manifest.json',
  './tailwind.js',
  './fonts/poppins_reg.ttf',
  './fonts/poppins_bold.ttf',
  './fonts/hind_reg.ttf',
  './fonts/hind_bold.ttf'
];

// Install: Cache critical assets individually so failure of one does not reject installation
self.addEventListener('install', (event) => {
  self.skipWaiting();
  event.waitUntil(
    caches.open(CACHE_NAME).then(async (cache) => {
      for (const asset of ASSETS_TO_CACHE) {
        try {
          await cache.add(asset);
        } catch (e) {
          console.warn('Asset caching deferred:', asset, e);
        }
      }
      try {
        await cache.add('./stories.js');
      } catch (e) {
        console.warn('stories.js caching deferred:', e);
      }
    })
  );
});

// Activate: Clean old caches and take immediate control
self.addEventListener('activate', (event) => {
  event.waitUntil(
    caches.keys().then((keys) => {
      return Promise.all(
        keys.map((key) => {
          if (key !== CACHE_NAME) {
            return caches.delete(key);
          }
        })
      );
    }).then(() => self.clients.claim())
  );
});

// Fetch Strategy:
// - HTML: Instant startup from Cache! Background revalidates.
// - stories.js: Cache-First with ignoreSearch (instant disk read), background updates.
// - Static Assets: Stale-While-Revalidate
self.addEventListener('fetch', (event) => {
  if (event.request.method !== 'GET') return;

  const url = new URL(event.request.url);
  const isHTML = event.request.mode === 'navigate' || url.pathname.endsWith('.html') || url.pathname.endsWith('/');
  const isStoriesJs = url.pathname.endsWith('stories.js');

  if (isHTML) {
    // Instant launch: Serve cached HTML immediately if available!
    event.respondWith(
      caches.match(event.request).then((cachedResponse) => {
        const fetchPromise = fetch(event.request).then((networkResponse) => {
          if (networkResponse && networkResponse.status === 200) {
            const responseClone = networkResponse.clone();
            caches.open(CACHE_NAME).then((cache) => {
              cache.put(event.request, responseClone);
            });
          }
          return networkResponse;
        }).catch(() => null);

        if (cachedResponse) {
          return cachedResponse;
        }

        return fetchPromise.then((netRes) => {
          if (netRes && netRes.status === 200) return netRes;
          return caches.match('./index.html').then(r => r || caches.match('./'));
        });
      })
    );
  } else if (isStoriesJs) {
    // Cache-First with background revalidation: Always load instantaneously from cache
    event.respondWith(
      caches.match(event.request, { ignoreSearch: true }).then((cachedResponse) => {
        const networkFetch = fetch(event.request).then((networkResponse) => {
          if (networkResponse && networkResponse.status === 200) {
            const responseClone = networkResponse.clone();
            caches.open(CACHE_NAME).then((cache) => {
              cache.put(event.request, responseClone);
            });
          }
          return networkResponse;
        }).catch(() => null);

        if (cachedResponse) {
          return cachedResponse;
        }

        return networkFetch.then((netRes) => {
          if (netRes && netRes.status === 200) return netRes;
          return caches.match('./stories.js', { ignoreSearch: true });
        });
      })
    );
  } else {
    // Stale-While-Revalidate for fonts, css, icons
    event.respondWith(
      caches.match(event.request).then((cachedResponse) => {
        const networkFetch = fetch(event.request).then((networkResponse) => {
          if (networkResponse && networkResponse.status === 200) {
            const responseClone = networkResponse.clone();
            caches.open(CACHE_NAME).then((cache) => {
              cache.put(event.request, responseClone);
            });
          }
          return networkResponse;
        }).catch(() => cachedResponse);

        return cachedResponse || networkFetch;
      })
    );
  }
});
