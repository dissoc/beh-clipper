// This shim is a necessary proxy to load the actual module compiled by CLJS,
// because a module cannot be loaded directly in a content script.
// See: https://stackoverflow.com/a/53033388/802303
(async () => {
    const src = chrome.runtime.getURL("js/content.js");
    const contentMain = await import(src);
    })();
