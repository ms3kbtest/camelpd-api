@ECHO OFF
kubectl delete -f kubernetes/camelpd-api-mysql.yaml
kubectl delete -f kubernetes/camelpd-api-deployment.yaml
kubectl delete -f kong/crd/camelpd-svc.yaml