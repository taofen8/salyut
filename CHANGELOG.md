## 0.0.8.RC1
> Released 2020-05-22

This is the first public version of salyut.

### Features

- Add new attributes `targets`,`target` to token (**- select:**,**- loop:**, **- find:**).
- Add new token **- mobile:** to support run firefox as mobile mode. 
- Deprecated token **- switchtmptab:**,**- closetmptab:**.

### Fixes

- Use token **- continue:** in token **- loop:** will cause dead looping. ([pr#28](https://github.com/shenruisi/salyut/pull/28))
- Can not compare a float type with a integer type in token **- if:**.([pr#28](https://github.com/shenruisi/salyut/pull/28))
- Can not set a value to a global path[[?](https://www.trico.cloud/tricoDoc/var/index.html),[issue#20](https://github.com/shenruisi/salyut/issues/20)].

## 0.0.8.RC2
> Released 2021-05-17

### Features
- Add new token [**- iframe:**](https://www.trico.cloud/doc/iframe/index.html)
- Add new token [**- safeclick:**](https://www.trico.cloud/doc/safeclick/index.html)
- Add new token [**- slide:**](https://www.trico.cloud/doc/slide/index.html)
- Add new token [**- walkthrough:**](https://www.trico.cloud/doc/walkthrough/index.html)
- Add new token [**- try:**](https://www.trico.cloud/doc/try/index.html)
