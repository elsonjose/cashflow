# using firebase-admin==5.2.0
from firebase_admin import storage, credentials, initialize_app
import sys

initialize_app(credentials.Certificate('cashflow_cloud_secret.json'),{
  'storageBucket': sys.argv[1],
})

bucket = storage.bucket()
blob = bucket.blob(sys.argv[2])
blob.upload_from_filename('app/build/outputs/apk/debug/app-debug.apk')
blob.make_public()
print(blob.public_url)