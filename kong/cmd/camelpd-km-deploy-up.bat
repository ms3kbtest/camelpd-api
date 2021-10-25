@ECHO OFF
kubectl apply -f kubernetes/camelpd-api-mysql.yaml
kubectl apply -f kubernetes/camelpd-api-deployment.yaml
kubectl apply -f kong/crd/camelpd-svc.yaml