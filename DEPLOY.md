Automating Deployment from DevCS to ACC
---------------------------------------

These instructions are a *temporary hack* to support deployment from
DevCS to ACC until built-in and fully integrated support is available
in DevCS.  I repeat--this is a temporary hack so please use with caution.


You can use `deploy.sh` as a *build step* in a DevCS Maven
build to push a generated application archive to ACC.

### Usage

`sh deploy.sh <id domain> <user id> <user password> <app name> <archive file>`

The `<archive file>` is assumed to be in the Maven generated `target` folder.

### Example
`sh deploy.sh jcsdemo888 demouser demopass MyApp myapp-dist.zip`

Installation
------------

Copy `deploy.sh` to the root of your DevCS project and commit to GIT so that it can be called from Maven.

Disclaimer
==========
Obviously hardcoding credentials is not a real solution but until DevCS
provides built-in deployment this is a workable solution for demos.
