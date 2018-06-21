// Generated by CoffeeScript 1.11.0
(function() {
  var httpd;

  httpd = void 0;

  document.addEventListener("deviceready", function() {
    var ref;
    return httpd = typeof cordova !== "undefined" && cordova !== null ? (ref = cordova.plugins) != null ? ref.CordovaUpdate : void 0 : void 0;
  });

  window.Servers = new ((function() {
    var servers;

    servers = {};

    function _Class() {
      this.loadCallbacks = [];
      document.addEventListener("deviceready", (function(_this) {
        return function() {
          return _this.load();
        };
      })(this));
    }

    _Class.prototype.random = function() {
      return Math.round(Math.random() * 10000000);
    };

    _Class.prototype.serverExists = function(url) {
      return servers[url] != null;
    };

    _Class.prototype.getServers = function() {
      var items, key, value;
      items = (function() {
        var results;
        results = [];
        for (key in servers) {
          value = servers[key];
          if (key !== 'active') {
            results.push({
              name: value.name,
              url: key
            });
          }
        }
        return results;
      })();
      return _.sortBy(items, 'name');
    };

    _Class.prototype.getServer = function(url) {
      return servers[url];
    };

    _Class.prototype.getActiveServer = function() {
      if ((servers.active != null) && (servers[servers.active] != null)) {
        return {
          url: servers.active,
          name: servers[servers.active].name,
          info: servers[servers.active].info
        };
      }
    };

    _Class.prototype.setActiveServer = function(url) {
      if (servers[url] != null) {
        servers.active = url;
        return this.save();
      }
    };

    _Class.prototype.validateUrl = function(url) {
      var atIndex, auth;
      if (!_.isString(url)) {
        console.error('url (', url, ') must be string');
        return {
          isValid: false,
          message: cordovai18n("The_address_provided_must_be_a_string"),
          url: url
        };
      }
      url = url.trim();
      url - url.replace(/\/$/, '');
      if (_.isEmpty(url)) {
        console.error('url (', url, ') can\'t be empty');
        return {
          isValid: false,
          message: cordovai18n("The_address_provided_can_not_be_empty"),
          url: url
        };
      }
      if (!/^https?:\/\/.+/.test(url)) {
        console.error('url (', url, ') must start with http:// or https://');
        return {
          isValid: false,
          message: cordovai18n("The_address_must_start_with_http_or_https"),
          url: url
        };
      }
      atIndex = url.indexOf('@');
      auth = null;
      if (atIndex !== -1) {
        auth = url.split('@')[0].split(':')[2];
      }
      return {
        isValid: true,
        message: cordovai18n("The_address_provided_is_valid"),
        url: url,
        auth: auth
      };
    };

    _Class.prototype.validateName = function(name) {
      if (!_.isString(name)) {
        console.error('name (', name, ') must be string');
        return {
          isValid: false,
          message: cordovai18n("The_name_provided_must_be_a_string"),
          name: name
        };
      }
      name = name.trim();
      if (_.isEmpty(name)) {
        console.error('name (', name, ') can\'t be empty');
        return {
          isValid: false,
          message: cordovai18n("The_name_provided_can_not_be_empty"),
          name: name
        };
      }
      return {
        isValid: true,
        message: cordovai18n("The_name_provided_is_valid"),
        name: name
      };
    };

    _Class.prototype.validateServer = function(url, auth, cb) {
      var headers, request, timeout;
      headers = {};
      if (auth) {
        headers.Authorization = "Bearer " + auth;
      }
      request = $.ajax({
        dataType: "json",
        url: url + "/api/info",
        headers: headers
      });
      timeout = setTimeout(function() {
        return request.abort();
      }, 5000);
      request.done(function(data, textStatus, jqxhr) {
        var j, len, version, versionMul, versionNum, versions;
        if ((data != null ? data.version : void 0) == null) {
          return cb(cordovai18n("The_address_provided_is_not_a_RocketChat_server"));
        }
        versions = data.version.split('.').reverse();
        versionNum = 0;
        versionMul = 1;
        for (j = 0, len = versions.length; j < len; j++) {
          version = versions[j];
          versionNum += version * versionMul;
          versionMul = versionMul * 1000;
        }
        if (versionNum < 7000) {
          return cb(cordovai18n("The_server_s_is_running_an_out_of_date_version_or_doesnt_support_mobile_applications_Please_ask_your_server_admin_to_update_to_a_new_version_of_RocketChat", url));
        }
        clearTimeout(timeout);
        return cb(null, data);
      });
      return request.fail(function(jqxhr, textStatus, error) {
        if ((error != null ? error.name : void 0) === "SyntaxError") {
          return cb(cordovai18n("The_server_s_is_running_an_out_of_date_version_Please_ask_your_server_admin_to_update_to_a_new_version_of_RocketChat", url));
        }
        console.log("req failed: " + textStatus + ", " + error + ", " + request.status);
        return cb(cordovai18n("Failed_to_connect_to_server_s_s", textStatus, error));
      });
    };

    _Class.prototype.getManifest = function(url, cb) {
      var urlObj;
      urlObj = this.validateUrl(url);
      if (!_.isFunction(cb)) {
        console.error('callback is required');
        return false;
      }
      if (urlObj.isValid === false) {
        return cb(urlObj.message);
      }
      return this.validateServer(urlObj.url, urlObj.auth, (function(_this) {
        return function(err, data) {
          var headers, request, timeout;
          if (err != null) {
            return cb(err);
          }
          headers = {};
          if (urlObj.auth) {
            headers.Authorization = "Bearer " + urlObj.auth;
          }
          request = $.ajax({
            dataType: "json",
            url: urlObj.url + "/__cordova/manifest.json",
            headers: headers
          });
          timeout = setTimeout(function() {
            return request.abort();
          }, 5000);
          request.done(function(data, textStatus, jqxhr) {
            var ref;
            if ((data != null ? (ref = data.manifest) != null ? ref.length : void 0 : void 0) > 0) {
              data.manifest.unshift({
                url: '/index.html?' + Math.round(Math.random() * 10000000),
                path: 'index.html',
                hash: Math.round(Math.random() * 10000000)
              });
              clearTimeout(timeout);
              return cb(null, data);
            } else {
              return cb(cordovai18n("The_server_s_is_not_enable_or_mobile_apps", urlObj.url));
            }
          });
          return request.fail(function(jqxhr, textStatus, error) {
            if (textStatus === 'parsererror') {
              return cb(cordovai18n("The_server_s_is_not_enable_or_mobile_apps", urlObj.url));
            } else {
              return cb(cordovai18n("Failed_to_connect_to_server_s_s", textStatus, error));
            }
          });
        };
      })(this));
    };

    _Class.prototype.registerServer = function(name, url, cb) {
      var nameObj, urlObj;
      nameObj = this.validateName(name);
      urlObj = this.validateUrl(url);
      if (urlObj.isValid === false) {
        return cb(urlObj.message);
      }
      if (nameObj.isValid === false) {
        return cb(nameObj.message);
      }
      return this.getManifest(url, (function(_this) {
        return function(err, info) {
          if (err) {
            return cb(err);
          }
          servers[url] = {
            name: name,
            info: info
          };
          return cb();
        };
      })(this));
    };

    _Class.prototype.updateServer = function(url, cb) {
      if (servers[url] == null) {
        console.error('invalid server url', url);
      }
      return this.getManifest(url, (function(_this) {
        return function(err, info) {
          var oldInfo;
          if (err) {
            return cb(err);
          }
          if (servers[url].info.version !== info.version) {
            oldInfo = servers[url].oldInfo;
            servers[url].oldInfo = servers[url].info;
            servers[url].info = info;
            return _this.downloadServer(url, function(status) {
              if ((status != null ? status.err : void 0) != null) {
                servers[url].info = servers[url].oldInfo;
                servers[url].oldInfo = oldInfo;
              }
              return cb(status);
            });
          }
        };
      })(this));
    };

    _Class.prototype.getFileTransfer = function() {
      if (this.fileTransfer == null) {
        this.fileTransfer = new FileTransfer();
      }
      return this.fileTransfer;
    };

    _Class.prototype.uriToPath = function(uri) {
      return decodeURI(uri).replace(/^file:\/\//g, '');
    };

    _Class.prototype.baseUrlToDir = function(baseUrl) {
      return encodeURIComponent(baseUrl.toLowerCase().replace(/[\s\.\\\/:@\-]/g, ''));
    };

    _Class.prototype.downloadServer = function(url, downloadServerCb) {
      var copiedFiles, copyDownloadedFiles, filesToCopy, found, from, initDownloadServer, item, j, k, len, len1, path, ref, ref1, results, to;
      copyDownloadedFiles = (function(_this) {
        return function(copyDownloadedFilesCb) {
          var copy, items;
          items = servers[url].info.manifest.filter(function(item) {
            return item.copied === false;
          });
          copy = function(item, cb) {
            return copyFile('file://' + item.copyFrom, item.copyTo, cb);
          };
          return async.each(items, copy, function(err) {
            return copyDownloadedFilesCb();
          });
        };
      })(this);
      initDownloadServer = (function(_this) {
        return function() {
          var download, i, items;
          i = 0;
          items = servers[url].info.manifest.filter(function(item) {
            return ((item != null ? item.url : void 0) != null) && item.downloaded !== true;
          });
          download = function(item, cb) {
            if ((item != null ? item.url : void 0) == null) {
              return cb();
            }
            if (item.downloaded === true) {
              return cb();
            }
            return _this.downloadFile(url, item.url.replace(/\?.+$/, ''), function(err, data) {
              item.downloaded = err === void 0;
              if (data != null) {
                item.copied = false;
                item.copyFrom = data.from;
                item.copyTo = data.to;
              }
              if (typeof downloadServerCb === "function") {
                downloadServerCb({
                  done: false,
                  count: ++i,
                  total: items.length
                });
              }
              return cb(err, data);
            });
          };
          return async.eachLimit(items, 5, download, function(err) {
            if (err != null) {
              return typeof downloadServerCb === "function" ? downloadServerCb({
                err: err
              }) : void 0;
            } else {
              return copyDownloadedFiles((function(_this) {
                return function() {
                  return typeof downloadServerCb === "function" ? downloadServerCb({
                    done: true,
                    count: items.length,
                    total: items.length
                  }) : void 0;
                };
              })(this));
            }
          });
        };
      })(this);
      filesToCopy = 0;
      copiedFiles = 0;
      if (servers[url].oldInfo != null) {
        ref = servers[url].info.manifest;
        for (j = 0, len = ref.length; j < len; j++) {
          item = ref[j];
          if (item.path.indexOf('packages/rocketchat_livechat/') > -1) {
            item.downloaded = true;
            continue;
          }
          found = null;
          servers[url].oldInfo.manifest.some(function(oldItem) {
            if (oldItem.path === item.path && oldItem.hash === item.hash) {
              found = oldItem;
              return true;
            }
          });
          if (found != null) {
            item.downloaded = true;
          }
        }
        return initDownloadServer();
      } else if ((typeof cacheManifest !== "undefined" && cacheManifest !== null ? cacheManifest.manifest : void 0) != null) {
        ref1 = servers[url].info.manifest;
        results = [];
        for (k = 0, len1 = ref1.length; k < len1; k++) {
          item = ref1[k];
          if (item.path.indexOf('packages/rocketchat_livechat/') > -1) {
            item.downloaded = true;
            continue;
          }
          found = null;
          cacheManifest.manifest.some(function(oldItem) {
            if (oldItem.path === item.path && oldItem.hash === item.hash) {
              found = oldItem;
              return true;
            }
          });
          if ((found != null ? found.url : void 0) != null) {
            path = found.url.replace(/\?.+$/, '');
            from = cordova.file.applicationDirectory + 'www/cache' + path;
            to = this.baseUrlToDir(url) + path;
            filesToCopy++;
            item.downloaded = true;
            results.push(copyFile(from, to, function() {
              copiedFiles++;
              if (filesToCopy === copiedFiles) {
                return initDownloadServer();
              }
            }));
          } else {
            results.push(void 0);
          }
        }
        return results;
      } else {
        return initDownloadServer();
      }
    };

    _Class.prototype.fixIndexFile = function(indexDir, baseUrl, cb) {
      var urlObj;
      urlObj = this.validateUrl(baseUrl);
      return readFile(indexDir, "index.html", (function(_this) {
        return function(err, file) {
          var schemalessUrl;
          if (err != null) {
            Bugsnag.notify("readIndexFileError", "Error fixing index file, index file read error", {
              err: err
            });
            return cb(err);
          }
          if (file == null) {
            Bugsnag.notify("readIndexFileNotFound", "Error fixing index file, index file not found");
            cb('index.html not found');
          }
          file = file.replace(/<script text="text\/javascript" src="\/shared\/.+\n/gm, '');
          file = file.replace(/<link rel="stylesheet" href="\/shared\/.+\n/gm, '');
          if (urlObj.auth) {
            schemalessUrl = baseUrl.replace('http://', '').replace('https://', '');
            file = file.replace(/(^\s*__meteor_runtime_config__ = JSON.+$)/gm, "	/* sandstorm workaround */ $1\n__meteor_runtime_config__.ROOT_URL = '" + baseUrl + "';\n__meteor_runtime_config__.DDP_DEFAULT_CONNECTION_URL = '" + baseUrl + "';\n__meteor_runtime_config__.SANDSTORM_API_TOKEN = '" + urlObj.auth + "';\n__meteor_runtime_config__.SANDSTORM_API_HOST = '" + baseUrl + "';\n\nwindow._OriginalWebSocket = window._OriginalWebSocket || window.WebSocket;\nwindow.WebSocket = function SandstormWebSocket (url, protocols) {\n	url = url.replace('" + schemalessUrl + "',\n	\"" + schemalessUrl + "/.sandstorm-token/" + urlObj.auth + "\");\n	if (protocols) {\n		return new _OriginalWebSocket(url, protocols);\n	} else {\n		return new _OriginalWebSocket(url);\n	}\n};");
          }
          file = file.replace(/(<\/head>)/gm, "<link rel=\"stylesheet\" href=\"/shared/css/servers-list.css\"/>\n<script text=\"text/javascript\" src=\"/shared/js/android_sender_id.js\"></script>\n<script text=\"text/javascript\" src=\"/shared/js/share.js\"></script>\n<script text=\"text/javascript\" src=\"/shared/js_compiled/i18n.js\"></script>\n<script text=\"text/javascript\" src=\"/shared/js_compiled/utils.js\"></script>\n<script text=\"text/javascript\" src=\"/shared/js_compiled/servers.js\"></script>\n<script text=\"text/javascript\" src=\"/shared/js_compiled/servers-list.js\"></script>\n$1");
          return writeFile(indexDir, "index.html", file, function() {
            return cb(null, file);
          });
        };
      })(this));
    };

    _Class.prototype.downloadFile = function(baseUrl, path, cb) {
      var attempts, ft, strippedUrl, tryDownload, urlObj;
      ft = this.getFileTransfer();
      attempts = 0;
      urlObj = this.validateUrl(baseUrl);
      strippedUrl = baseUrl;
      if (urlObj.auth) {
        if (baseUrl) {
          strippedUrl = baseUrl.replace(("sandstorm:" + urlObj.auth + "@").toLowerCase(), '');
        }
        if (path) {
          path = path.replace(("sandstorm:" + urlObj.auth + "@").toLowerCase(), '');
        }
      }
      tryDownload = (function(_this) {
        return function() {
          var downloadError, downloadSuccess, headers, pathToSave, pathToSaveFinal, url;
          attempts++;
          url = encodeURI(strippedUrl + "/__cordova" + path + "?" + (_this.random()));
          pathToSave = _this.uriToPath(cordova.file.dataDirectory) + _this.baseUrlToDir(baseUrl) + '_temp' + encodeURI(path);
          pathToSaveFinal = _this.uriToPath(cordova.file.dataDirectory) + _this.baseUrlToDir(baseUrl) + encodeURI(path);
          downloadSuccess = function(entry) {
            if (entry != null) {
              console.log("done downloading " + path);
              return cb(null, {
                from: pathToSave,
                to: _this.baseUrlToDir(baseUrl) + encodeURI(path)
              });
            }
          };
          downloadError = function(err) {
            if (attempts < 5) {
              console.log("Trying (" + attempts + ") " + url);
              return tryDownload();
            }
            console.log('downloadFile err', err);
            return cb(err, null);
          };
          headers = {};
          if (urlObj.auth) {
            headers.Authorization = "Bearer " + urlObj.auth;
          }
          return ft.download(url, pathToSave, downloadSuccess, downloadError, true, {
            headers: headers
          });
        };
      })(this);
      return tryDownload();
    };

    _Class.prototype.save = function(cb) {
      return writeFile(cordova.file.dataDirectory, 'servers.json', JSON.stringify(servers), function(err, data) {
        if (err != null) {
          console.log('Error saving servers file', err);
        }
        return typeof cb === "function" ? cb() : void 0;
      });
    };

    _Class.prototype.load = function() {
      var timer;
      timer = setTimeout(this.load.bind(this), 2000);
      return readFile(cordova.file.dataDirectory, 'servers.json', (function(_this) {
        return function(err, savedServers) {
          var cb, j, len, ref, results;
          clearTimeout(timer);
          if ((savedServers != null ? savedServers.length : void 0) > 2) {
            servers = JSON.parse(savedServers);
          }
          _this.loaded = true;
          ref = _this.loadCallbacks;
          results = [];
          for (j = 0, len = ref.length; j < len; j++) {
            cb = ref[j];
            results.push(cb());
          }
          return results;
        };
      })(this));
    };

    _Class.prototype.onLoad = function(cb) {
      if (this.loaded === true) {
        return cb();
      }
      return this.loadCallbacks.push(cb);
    };

    _Class.prototype.clear = function() {
      localStorage.setItem('servers', JSON.stringify({}));
      return servers = {};
    };

    _Class.prototype.startLocalServer = function(path, cb) {
      var failure, options, success;
      if ((cb == null) && typeof path === 'function') {
        cb = path;
        path = '';
      }
      if (httpd == null) {
        return console.error('CorHttpd plugin not available/ready.');
      }
      options = {
        'www_root': this.uriToPath(cordova.file.applicationDirectory + 'www/'),
        'cordovajs_root': this.uriToPath(cordova.file.applicationDirectory + 'www/'),
        'host': 'localhost.local'
      };
      success = (function(_this) {
        return function(url) {
          console.log("server is started:", options.host);
          if (typeof cb === "function") {
            cb(null, options.host);
          }
          return location.href = "http://" + options.host + "/" + path;
        };
      })(this);
      failure = function(error) {
        if (typeof cb === "function") {
          cb(error);
        }
        return console.log('failed to start server:', error);
      };
      return httpd.startServer(options, success, failure);
    };

    _Class.prototype.startServer = function(baseUrl, path, cb) {
      var failure, options, success;
      if ((cb == null) && typeof path === 'function') {
        cb = path;
        path = '';
      }
      if (httpd == null) {
        return console.error('CorHttpd plugin not available/ready.');
      }
      if (servers[baseUrl] == null) {
        return console.error('Invalid baseUrl');
      }
      options = {
        'www_root': this.uriToPath(cordova.file.dataDirectory) + this.baseUrlToDir(baseUrl),
        'cordovajs_root': this.uriToPath(cordova.file.applicationDirectory + 'www/'),
        'host': this.baseUrlToDir(baseUrl) + '.meteor.local'
      };
      success = (function(_this) {
        return function(url) {
          console.log("server is started:", url);
          servers.active = baseUrl;
          return _this.save(function() {
            if (typeof cb === "function") {
              cb(null, baseUrl);
            }
            return location.href = "http://" + options.host + "/" + path;
          });
        };
      })(this);
      failure = function(error) {
        if (typeof cb === "function") {
          cb(error);
        }
        return console.log('failed to start server:', error);
      };
      return this.fixIndexFile(cordova.file.dataDirectory + this.baseUrlToDir(baseUrl), baseUrl, function(error) {
        if (error != null) {
          if (typeof cb === "function") {
            cb(error);
          }
        }
        return httpd.startServer(options, success, failure);
      });
    };

    _Class.prototype.deleteServer = function(url, cb) {
      if (servers[url] == null) {
        return;
      }
      delete servers[url];
      if (servers.active === url) {
        delete servers.active;
      }
      removeDir(cordova.file.dataDirectory + this.baseUrlToDir(url));
      return this.save(cb);
    };

    return _Class;

  })());

}).call(this);
