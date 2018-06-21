// Generated by CoffeeScript 1.11.0
(function() {
  var language, loaded,
    slice = [].slice;

  window.languages = {};

  window.loadI18nLanguage = function(language, cb) {
    var request, url;
    url = "shared/i18n/" + language + ".i18n.json";
    if (location.protocol === 'http:') {
      url = '/' + url;
    }
    request = $.getJSON(url);
    request.done(function(data) {
      window.languages[language] = data;
      return typeof cb === "function" ? cb(null, data) : void 0;
    });
    return request.fail(function(err) {
      return typeof cb === "function" ? cb(err) : void 0;
    });
  };

  language = navigator.language.split('-').shift();

  window.loadI18n = function(cb) {
    return loadI18nLanguage('en', function() {
      if (window.languages[language] == null) {
        return loadI18nLanguage(language, cb);
      } else {
        return cb();
      }
    });
  };

  window.cordovai18n = function() {
    var args, ref, ref1, string;
    string = arguments[0], args = 2 <= arguments.length ? slice.call(arguments, 1) : [];
    if (string == null) {
      return;
    }
    if (((ref = window.languages[language]) != null ? ref[string] : void 0) != null) {
      string = window.languages[language][string];
    } else if (((ref1 = window.languages.en) != null ? ref1[string] : void 0) != null) {
      string = window.languages.en[string];
    }
    while (string.indexOf('%s') > -1) {
      string = string.replace('%s', args.shift());
    }
    return string;
  };

  loaded = false;

  window.addEventListener('load', function() {
    return loaded = true;
  });

  window.updateHtml = function() {
    var i, item, len, ref, results;
    ref = $('[data-i18n]');
    results = [];
    for (i = 0, len = ref.length; i < len; i++) {
      item = ref[i];
      results.push(item.innerHTML = cordovai18n($(item).data('i18n')));
    }
    return results;
  };

  loadI18n(function() {
    if (loaded === true) {
      return updateHtml();
    } else {
      return window.addEventListener('load', function() {
        return updateHtml();
      });
    }
  });

}).call(this);