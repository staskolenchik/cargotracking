module.exports = {
  'root': true,
  'env': {
    'node': true,
    'es6': true
  },
  'extends': [
    'plugin:vue/essential',
    'plugin:import/warnings',
    'plugin:import/errors',
    'eslint:recommended'
  ],
  'rules': {
    'quotes': [
      'error',
      'single',
      {
        'avoidEscape': true
      }
    ],
    'comma-style': [
      2,
      'last'
    ],
    'comma-dangle': [
      2,
      'never'
    ],
    'semi': [
      2,
      'always'
    ],
    'import/order': [2, {
      'groups': [
        'external',
        ['sibling', 'parent'],
        'index'
      ],
      'newlines-between': 'always'
    }],
    'import/no-unresolved': 'off'
  },
  'parserOptions': {
    'parser': 'babel-eslint'
  }
};
