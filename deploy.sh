#!/bin/sh
#
#*******************************************************************************
# /*
#  * File: deploy.sh
#  *
#  * Copyright (c) 2016 Oracle and/or its affiliates.
#  *
#  * You may not use this file except in compliance with the Universal Permissive
#  * License (UPL), Version 1.0 (the "License.")
#  *
#  * You may obtain a copy of the License at https://opensource.org/licenses/UPL.
#  *
#  * Unless required by applicable law or agreed to in writing, software distributed
#  * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
#  * CONDITIONS OF ANY KIND, either express or implied.
#  *
#  * See the License for the specific language governing permissions and limitations
#  * under the License.
#  */
#
#  @author Phil Chung
#*******************************************************************************

export ID_DOMAIN=$1
export USER_ID=$2
export USER_PASSWORD=$3
export APP_NAME=$4
export ARCHIVE_FILE=$5
export ARCHIVE_LOCAL=target/$ARCHIVE_FILE
export APAAS_HOST=apaas.us.oraclecloud.com

# CREATE CONTAINER
echo '\n[info] Creating container\n'
curl -i -X PUT \
    -u ${USER_ID}:${USER_PASSWORD} \
    https://${ID_DOMAIN}.storage.oraclecloud.com/v1/Storage-$ID_DOMAIN/$APP_NAME

# PUT ARCHIVE IN STORAGE CONTAINER
echo '\n[info] Uploading application to storage\n'
curl -i -X PUT \
  -u ${USER_ID}:${USER_PASSWORD} \
  https://${ID_DOMAIN}.storage.oraclecloud.com/v1/Storage-$ID_DOMAIN/$APP_NAME/$ARCHIVE_FILE \
      -T $ARCHIVE_LOCAL

# See if application exists
let httpCode=`curl -i -X GET  \
  -u ${USER_ID}:${USER_PASSWORD} \
  -H "X-ID-TENANT-NAME:${ID_DOMAIN}" \
  -H "Content-Type: multipart/form-data" \
  -sL -w "%{http_code}" \
  https://${APAAS_HOST}/paas/service/apaas/api/v1.1/apps/${ID_DOMAIN}/${APP_NAME} \
  -o /dev/null`

# If application exists...
if [ $httpCode == 200 ]
then
  # Update application
  echo '\n[info] Updating application...\n'
  curl -i -X PUT  \
    -u ${USER_ID}:${USER_PASSWORD} \
    -H "X-ID-TENANT-NAME:${ID_DOMAIN}" \
    -H "Content-Type: multipart/form-data" \
    -F archiveURL=${APP_NAME}/${ARCHIVE_FILE} \
    https://${APAAS_HOST}/paas/service/apaas/api/v1.1/apps/${ID_DOMAIN}/${APP_NAME}
else
  # Create application and deploy
  echo '\n[info] Creating application...\n'
  curl -i -X POST  \
    -u ${USER_ID}:${USER_PASSWORD} \
    -H "X-ID-TENANT-NAME:${ID_DOMAIN}" \
    -H "Content-Type: multipart/form-data" \
    -F "name=${APP_NAME}" \
    -F "runtime=java" \
    -F "subscription=Hourly" \
    -F archiveURL=${APP_NAME}/${ARCHIVE_FILE} \
    https://${APAAS_HOST}/paas/service/apaas/api/v1.1/apps/${ID_DOMAIN}
fi
echo '\n[info] Deployment complete\n'
