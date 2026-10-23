// copies the sql.js wasm binary next to the bundle; the sqljs web worker loads it from there
const CopyWebpackPlugin = require("copy-webpack-plugin");
config.plugins.push(
    new CopyWebpackPlugin({
        patterns: [
            "../../node_modules/sql.js/dist/sql-wasm.wasm"
        ]
    })
);

// sql.js is a universal package that also mentions node modules; none of them are needed in the browser
config.resolve = config.resolve || {};
config.resolve.fallback = Object.assign(config.resolve.fallback || {}, {
    fs: false,
    path: false,
    crypto: false
});
