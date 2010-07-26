(ns myapp.servlet
(:gen-class :extends javax.servlet.http.HttpServlet)
(:require [compojure.route :as route])
(:use ring.util.servlet [myapp.routes :only [app]]))

(defservice app)