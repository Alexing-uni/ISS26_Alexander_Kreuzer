### conda install diagrams
from diagrams import Cluster, Diagram, Edge
from diagrams.custom import Custom
import os
os.environ['PATH'] += os.pathsep + 'C:/Program Files/Graphviz/bin/'

graphattr = {     #https://www.graphviz.org/doc/info/attrs.html
    'fontsize': '22',
}

nodeattr = {   
    'fontsize': '22',
    'bgcolor': 'lightyellow'
}

eventedgeattr = {
    'color': 'red',
    'style': 'dotted'
}
evattr = {
    'color': 'darkgreen',
    'style': 'dotted'
}
with Diagram('orchestrateddemoArch', show=False, outformat='png', graph_attr=graphattr) as diag:
  with Cluster('env'):
     sys = Custom('','./qakicons/system.png')
### see https://renenyffenegger.ch/notes/tools/Graphviz/attributes/label/HTML-like/index
     with Cluster('ctxorch', graph_attr=nodeattr):
          director=Custom('director','./qakicons/symActorWithobjSmall.png')
          painter=Custom('painter','./qakicons/symActorWithobjSmall.png')
          displaymock=Custom('displaymock','./qakicons/symActorWithobjSmall.png')
     director >> Edge(color='magenta', style='solid', decorate='true', label='<paint<font color="darkgreen"> painted</font> &nbsp; >',  fontcolor='magenta') >> painter
     painter >> Edge(color='blue', style='solid',  decorate='true', label='<cellstate &nbsp; >',  fontcolor='blue') >> displaymock
diag
